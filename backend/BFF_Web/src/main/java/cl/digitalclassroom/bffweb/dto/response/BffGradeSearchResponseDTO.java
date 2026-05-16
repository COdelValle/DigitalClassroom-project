package cl.digitalclassroom.bffweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BffGradeSearchResponseDTO {
    private Long gradeId;
    private Double gradeValue;
    private String title;
    private LocalDate date;

    private Long studentId;
    private String studentName;
    private String studentRut;

    private Long courseId;
    private String subjectName;
}
