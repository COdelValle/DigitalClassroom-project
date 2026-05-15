package cl.digitalclassroom.classroommanager.model.dto.request.modify;

import lombok.Data;

@Data
public class CourseModifyRequestDTO {
    private Long id;
    private String teacherName;
    private String semester;
}
