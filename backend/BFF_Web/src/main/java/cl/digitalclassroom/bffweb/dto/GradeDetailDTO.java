package cl.digitalclassroom.bffweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDetailDTO {
    private Long gradeId;
    private Double value;
    private String title;
}
