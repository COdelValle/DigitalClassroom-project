package cl.digitalclassroom.bffweb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectGradeDTO {
    private Long courseId;
    private String subjectName;
    private String teacherName;
    private List<GradeDetailDTO> individualGrades;
    private Double average;
}
