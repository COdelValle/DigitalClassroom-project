package cl.digitalclassroom.bffweb.dto.request;

import lombok.Data;

@Data
public class GradeRequestDTO {
    private Long studentId;
    private Long assessmentId;
    private Double score;
}
