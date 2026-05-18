package cl.digitalclassroom.assessmentmanager.model.dto.request.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO para modificar un encargo")
public class AssessmentModifyRequestDTO {
    
    @Schema(example = "Ensayo sobre el Quijote")
    @Size(min = 3, max = 255, message = "El título debe tener entre 3 y 255 caracteres")
    private String title;
    
    @Positive(message = "El ID del curso debe ser positivo")
    private Long courseId;
    
    @FutureOrPresent(message = "La fecha del examen no puede ser pasada")
    private LocalDate examDate;
}
