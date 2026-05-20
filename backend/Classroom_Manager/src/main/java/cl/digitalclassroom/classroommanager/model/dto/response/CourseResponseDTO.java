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
public class CourseResponseDTO {
    private Long id;
    private SubjectResponseDTO subject;
    private Long classroomId;
    private String classroomName;
    private Integer schoolYear;
    private String semester;
    private String teacherName;

    public static CourseResponseDTO fromEntity(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .subject(SubjectResponseDTO.fromEntity(course.getSubject()))
                .classroomId(course.getClassroom().getId())
                .classroomName(course.getClassroom().getName())
                .schoolYear(course.getSchoolYear())
                .semester(course.getSemester())
                .teacherName(course.getTeacherName())
                .build();
    }
}
