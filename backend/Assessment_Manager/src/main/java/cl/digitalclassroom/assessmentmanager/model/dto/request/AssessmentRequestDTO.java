package cl.digitalclassroom.assessmentmanager.model.dto.request;

import cl.digitalclassroom.assessmentmanager.validation.ChileanGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "DTO para crear un encargo, con o sin notas iniciales")
public class AssessmentRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 255, message = "El título debe tener entre 3 y 255 caracteres")
    @Schema(example = "Ensayo sobre el Quijote")
    private String title;

    @NotNull(message = "El ID del curso es obligatorio")
    @Positive(message = "El ID del curso debe ser positivo")
    @Schema(example = "1")
    private Long courseId;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha del examen no puede ser pasada")
    @Schema(example = "2026-06-18")
    private LocalDate examDate;

    @Valid
    @Size(max = 500, message = "No se pueden agregar más de 500 calificaciones iniciales")
    private List<GradeItemDTO> grades;

    @Data
    public static class GradeItemDTO {
        @NotNull(message = "El ID del estudiante es obligatorio")
        @Positive(message = "El ID del estudiante debe ser positivo")
        private Long studentId;

        @ChileanGrade(message = "La calificación debe estar entre 1.0 y 7.0")
        private Double score;
    }
}
