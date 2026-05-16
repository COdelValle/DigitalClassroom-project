package cl.digitalclassroom.bffweb.dto.external;

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
}
