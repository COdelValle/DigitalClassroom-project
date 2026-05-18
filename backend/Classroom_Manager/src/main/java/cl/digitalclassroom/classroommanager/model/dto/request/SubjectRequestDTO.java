package cl.digitalclassroom.classroommanager.model.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequestDTO {
    @NotBlank(message = "El nombre de la materia es obligatorio")
    private String name;

    @NotBlank(message = "El área es obligatoria")
    private String area;
}

