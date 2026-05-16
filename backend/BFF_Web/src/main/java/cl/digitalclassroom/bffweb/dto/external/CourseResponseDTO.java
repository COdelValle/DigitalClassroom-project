package cl.digitalclassroom.bffweb.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private SubjectResponseDTO subject;
    private Long classroomId;
    private String classroomName;
    private Integer schoolYear;
    private String semester;
    private String teacherName;
}
