package cl.digitalclassroom.classroommanager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {
    @NotNull(message = "El ID de la materia es obligatorio")
    private Long subjectId;

    @NotNull(message = "El ID del aula es obligatorio")
    private Long classroomId;

    @NotNull(message = "El año escolar es obligatorio")
    private Integer schoolYear;

    @NotBlank(message = "El semestre es obligatorio")
    private String semester;

    @NotBlank(message = "El nombre del profesor es obligatorio")
    private String teacherName;
}
