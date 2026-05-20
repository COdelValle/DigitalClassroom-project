package cl.digitalclassroom.studentmanager.model.dto.request;

import cl.digitalclassroom.studentmanager.validation.RUT;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestDTO {

    @NotBlank(message = "El RUT es requerido")
    @RUT
    private String rut;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50)
    private String firstName;

    private String middleName;

    @NotBlank(message = "El apellido es requerido")
    @Size(min = 2, max = 100)
    private String lastName;

    @NotNull(message = "Fecha de nacimiento requerida")
    @Past
    private Date birthDate;

    @NotEmpty(message = "Debe registrar al menos una alergia o 'Ninguna'")
    private List<String> allergies;

    @NotEmpty(message = "Debe haber al menos un representante")
    @Valid
    private List<LegalRepresentativeDTO> legalRepresentatives;
}
