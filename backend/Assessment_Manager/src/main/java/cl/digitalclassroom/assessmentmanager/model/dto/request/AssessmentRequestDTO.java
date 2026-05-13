package cl.digitalclassroom.assessmentmanager.model.dto.request;

import cl.digitalclassroom.assessmentmanager.validation.ChileanGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "DTO para crear un encargo, con o sin notas iniciales")
public class AssessmentRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Schema(example = "Ensayo sobre el Quijote")
    private String title;

    @NotBlank(message = "El ID de asignatura es obligatorio")
    @Schema(example = "MAT-1")
    private String subjectId;

    @NotBlank(message = "El ID de clase es obligatorio")
    @Schema(example = "2mA")
    private String classId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(example = "2026-06-18")
    private LocalDate examDate;

    @Valid
    private List<GradeItemDTO> grades;

    @Data
    public static class GradeItemDTO {
        @NotNull
        private Long studentId;

        @ChileanGrade
        private Double score;
    }
}
