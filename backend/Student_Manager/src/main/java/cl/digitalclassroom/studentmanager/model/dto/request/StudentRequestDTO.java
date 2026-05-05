package cl.digitalclassroom.studentmanager.model.dto.request;

import cl.digitalclassroom.studentmanager.Validation.RUT;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO para la solicitud de creación o actualización de un estudiante.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestDTO {

    /**
     * RUT del estudiante. Debe ser un RUT válido chileno.
     */
    @Schema(example = "12.345.678-9", description = "RUT del estudiante (formato: XX.XXX.XXX-X)")
    @NotBlank(message = "El RUT es requerido")
    @RUT
    private String rut;

    /**
     * Primer nombre del estudiante.
     */
    @Schema(example = "juan", description = "Primer nombre del estudiante")
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50)
    private String firstName;

    /**
     * Segundo nombre del estudiante (opcional).
     */
    @Schema(example = "carlos", description = "Segundo nombre del estudiante (opcional)")
    private String middleName;

    /**
     * Apellidos del estudiante.
     */
    @Schema(example = "García López", description = "Apellido(s) del estudiante")
    @NotBlank(message = "El apellido es requerido")
    @Size(min = 2, max = 100)
    private String lastName;

    /**
     * Fecha de nacimiento del estudiante.
     */
    @Schema(example = "2010-05-15T00:00:00Z", description = "Fecha de nacimiento (debe ser en el pasado)")
    @NotNull(message = "Fecha de nacimiento requerida")
    @Past
    private Date birthDate;

    /**
     * Lista de alergias del estudiante.
     */
    @Schema(example = "[\"Maní\", \"Camarones\"]", description = "Lista de alergias (mínimo una)")
    @NotEmpty(message = "Debe registrar al menos una alergia o 'Ninguna'")
    private List<String> allergies;

    /**
     * Lista de representantes legales del estudiante.
     */
    @Schema(description = "Lista de representantes legales (mínimo uno)")
    @NotEmpty(message = "Debe haber al menos un representante")
    @Valid
    private List<LegalRepresentativeDTO> legalRepresentatives;
}
