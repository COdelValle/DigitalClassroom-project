package cl.digitalclassroom.bffweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllStudentsGradesSummaryDTO {
    private Long studentId;
    private String rut;
    private String fullName;
    private Double generalAverage;
    private Integer failedSubjects;
    private String status;
}