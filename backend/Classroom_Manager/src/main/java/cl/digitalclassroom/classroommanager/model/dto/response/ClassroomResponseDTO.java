package cl.digitalclassroom.classroommanager.model.dto.response;

import cl.digitalclassroom.classroommanager.model.entity.Classroom;
import cl.digitalclassroom.classroommanager.model.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponseDTO {
    private Long id;
    private String code;
    private String name;
    private Integer schoolYear;

    // Lista de IDs de estudiantes
    private List<Long> studentIds;

    // Lista de cursos/materias asociados a esta aula
    private List<CourseSummaryDTO> courses;

    public static ClassroomResponseDTO fromEntity(Classroom classroom, List<Course> courses) {
        return ClassroomResponseDTO.builder()
                .id(classroom.getId())
                .code(classroom.getCode())
                .name(classroom.getName())
                .schoolYear(classroom.getSchoolYear())
                .studentIds(classroom.getStudentIds())
                .courses(courses != null ?
                        courses.stream().map(CourseSummaryDTO::fromEntity).toList() :
                        Collections.emptyList())
                .build();
    }
}
