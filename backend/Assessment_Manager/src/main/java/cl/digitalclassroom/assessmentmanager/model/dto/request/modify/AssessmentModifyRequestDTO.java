package cl.digitalclassroom.assessmentmanager.model.dto.request.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO para modificar un encargo")
public class AssessmentModifyRequestDTO {
    private Long id;

    @Schema(example = "Ensayo sobre el Quijote")
    private String title;
    private Long courseId;
    private LocalDate examDate;
}
