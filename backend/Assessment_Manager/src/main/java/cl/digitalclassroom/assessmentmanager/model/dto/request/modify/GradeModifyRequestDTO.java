package cl.digitalclassroom.assessmentmanager.model.dto.request.modify;

import cl.digitalclassroom.assessmentmanager.validation.ChileanGrade;
import lombok.Data;

@Data
public class GradeModifyRequestDTO {
    private Long id;

    @ChileanGrade
    private Double score;
    private Long studentId;
}
