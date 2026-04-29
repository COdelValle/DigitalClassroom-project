package cl.digitalclassroom.studentmanager.model.dto;

import cl.digitalclassroom.studentmanager.Validation.Phone;
import cl.digitalclassroom.studentmanager.Validation.RUT;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalRepresentativeDTO {
    @NotBlank(message = "El RUT del representante no puede esta vacio")
    @NotNull(message = "El RUT del representante es obligatorio")
    @RUT(message = "RUT no valido")
    private String RUT;

    @NotBlank(message = "El nombre del representante es obligatorio")
    private String fullName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido")
    private String email;

    @Size(min = 1, message = "Debe proporcionar al menos un número de teléfono")
    private List<@Phone String> phoneNumber;

    @NotBlank(message = "El parentesco o relación es obligatorio")
    private String relationship; // Ejemplo: Padre, Madre, Tutor Legal
}