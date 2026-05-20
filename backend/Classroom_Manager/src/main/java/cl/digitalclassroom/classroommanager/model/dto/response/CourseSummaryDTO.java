package cl.digitalclassroom.classroommanager.model.dto.response;

import cl.digitalclassroom.classroommanager.model.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryDTO {
    private Long id;
    private String subjectName; // Ej: "Matemáticas"
    private String teacherName; // Ej: "Juan Pérez"
    private String semester;

    public static CourseSummaryDTO fromEntity(Course course) {
        return CourseSummaryDTO.builder()
                .id(course.getId())
                .subjectName(course.getSubject().getName())
                .teacherName(course.getTeacherName())
                .semester(course.getSemester())
                .build();
    }
}
