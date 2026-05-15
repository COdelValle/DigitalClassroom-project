package cl.digitalclassroom.classroommanager.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomRequestDTO {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    @NotBlank(message = "El código del curso es obligatorio")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código solo debe contener mayúsculas, números y guiones")
    private String code;

    @NotNull(message = "El año escolar es obligatorio")
    @Min(value = 2024, message = "El año escolar debe ser igual o mayor a 2024")
    private Integer schoolYear;

    @NotEmpty(message = "La lista de estudiantes no puede estar vacía")
    private List<Long> studentIds;
}