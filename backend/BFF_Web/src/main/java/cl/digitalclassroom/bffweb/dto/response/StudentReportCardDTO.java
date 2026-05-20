package cl.digitalclassroom.bffweb.dto.response;

import cl.digitalclassroom.bffweb.dto.SubjectGradeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportCardDTO {
    private Long studentId;
    private String studentName;
    private String rut;
    private String classroomName;
    private List<SubjectGradeDTO> subjects;
    private Double finalAverage;
}
