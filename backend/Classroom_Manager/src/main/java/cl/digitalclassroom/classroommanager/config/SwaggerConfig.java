package cl.digitalclassroom.classroommanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI ClassroomManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Classroom Manager API")
                        .description("API para la gestión de Asignaturas y Cursos(Relación entre diferentes cursos, como 1medioA o 1medioB, con las asignaturas que se les imparten) - Digital Classroom")
                        .version("v1.0"));
    }
}