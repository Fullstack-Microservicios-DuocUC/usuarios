package cl.duoc.mineria.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){

        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Usuarios")
                        .version("1.0.0")
                        .description("Microservicio para la gestión del ciclo de vida de los usuarios y sus roles.")
                    );

    }

}
