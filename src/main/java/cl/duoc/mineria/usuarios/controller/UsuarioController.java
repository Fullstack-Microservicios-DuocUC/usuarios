package cl.duoc.mineria.usuarios.controller;

import cl.duoc.mineria.usuarios.dto.ActualizarRolUsuarioDTO;
import cl.duoc.mineria.usuarios.dto.RegistrarUsuarioDTO;
import cl.duoc.mineria.usuarios.exception.UsuarioNotFoundException;
import cl.duoc.mineria.usuarios.model.RolUsuario;
import cl.duoc.mineria.usuarios.model.Usuario;
import cl.duoc.mineria.usuarios.service.ServiceUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Gestión de Usuarios", description = "Operaciones para administrar el ciclo de vida de los usuarios del sistema.")
public class UsuarioController {

    private final ServiceUsuario usuarioService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. RUT ya existe, campos nulos)")
    })
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistrarUsuarioDTO dto) {
        return new ResponseEntity<>(usuarioService.registrarUsuario(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista completa de todos los usuarios registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida con éxito")
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su ID", description = "Busca y devuelve un usuario específico a partir de su ID numérico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el ID proporcionado")
    })
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}/rol")
    @Operation(summary = "Actualizar el rol de un usuario", description = "Modifica el rol de un usuario existente, útil para promover o cambiar funciones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol del usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el ID proporcionado")
    })
    public ResponseEntity<Usuario> actualizarRolUsuario(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRolUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarRol(id, dto));
    }

    @GetMapping("/existe/{id}")
    @Operation(summary = "Verificar si un usuario existe", description = "Endpoint de utilidad para otros microservicios. Devuelve 'true' si el usuario con el ID existe, 'false' en caso contrario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada, el cuerpo de la respuesta contiene el booleano")
    })
    public ResponseEntity<Boolean> verificarExiste(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.existeUsuario(id));
    }

    @GetMapping("/paleros/{id}")
    @Operation(summary = "Verificar si un usuario es un Operador de Pala activo",
               description = "Endpoint de negocio para el microservicio de Ciclo de Transporte. Valida si un ID de usuario corresponde a un 'OPERADOR_PALA'. Devuelve 'true' si el usuario existe y tiene ese rol, 'false' en cualquier otro caso (no existe, tiene otro rol, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada, el cuerpo de la respuesta contiene el booleano")
    })

    public ResponseEntity<Boolean> verificarEsPaleroActivo(@PathVariable Long id) {
        try {
            // 1. Obtienes el usuario directamente (si no existe, saltará al catch)
            Usuario usuario = usuarioService.obtenerPorId(id);
            
            // 2. Comparas el rol directamente usando el objeto obtenido
            boolean esPaleroValido = usuario.getRol() == RolUsuario.OPERADOR_PALA;
            
            return ResponseEntity.ok(esPaleroValido);
            
        } catch (UsuarioNotFoundException e) {
            // Si el service lanzó la excepción porque el ID no existe, retornamos false
            System.out.println("[Usuarios] El ID " + id + " no existe en el sistema.");
            return ResponseEntity.ok(false);
        }
    }
}