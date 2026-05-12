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
@Schema(description = "DTO para modificar un encargo")
public class AssessmentModifyRequestDTO {
    private Long id;

    @Schema(example = "Ensayo sobre el Quijote")
    private String title;
    private String subjectId;
    private String classId;
    private LocalDate examDate;
}
