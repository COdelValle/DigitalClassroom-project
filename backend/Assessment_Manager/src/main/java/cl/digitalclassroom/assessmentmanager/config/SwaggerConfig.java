package cl.digitalclassroom.assessmentmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI AssessmentManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Assessment Manager API")
                        .description("API para la gestión de encargos y notas - Digital Classroom")
                        .version("v1.0"));
    }
}