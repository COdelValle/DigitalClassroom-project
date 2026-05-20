package cl.digitalclassroom.studentmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI studentManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Manager API")
                        .description("API para la gestión de estudiantes y representantes legales - Digital Classroom")
                        .version("v1.0"));
    }
}