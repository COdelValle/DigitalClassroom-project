package cl.digitalclassroom.bffweb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI BFFWebOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BFF Web API")
                        .description("API para la gestión de la conexión entre Backend y Frontend - Digital Classroom")
                        .version("v1.0"));
    }
}