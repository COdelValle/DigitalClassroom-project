package cl.digitalclassroom.assessmentmanager.model.dto.request;

import cl.digitalclassroom.assessmentmanager.validation.ChileanGrade;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequestDTO {
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;

    @NotNull(message = "El ID de la evaluación es obligatorio")
    private Long assessmentId;

    @ChileanGrade
    private Double score;
}
