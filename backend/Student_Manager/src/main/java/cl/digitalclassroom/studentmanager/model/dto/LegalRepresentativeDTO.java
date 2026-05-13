package cl.digitalclassroom.studentmanager.model.dto;

import cl.digitalclassroom.studentmanager.validation.Phone;
import cl.digitalclassroom.studentmanager.validation.RUT;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
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
@Embeddable
public class LegalRepresentativeDTO {
    @Schema(example = "12.345.678-9")
    @NotBlank(message = "El RUT del representante no puede esta vacio")
    @NotNull(message = "El RUT del representante es obligatorio")
    @RUT(message = "RUT no valido")
    private String rut;

    @Schema(example = "Juan Pérez")
    @NotBlank(message = "El nombre del representante es obligatorio")
    private String fullName;

    @Schema(example = "juan.p@gmail.com")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido")
    private String email;

    @Schema(example = "+56 9 1111 2222")
    @Size(min = 1, message = "Debe proporcionar al menos un número de teléfono")
    private List<@Phone String> phoneNumber;

    @Schema(example = "Padre, Madre, Tutor Legal")
    @NotBlank(message = "El parentesco o relación es obligatorio")
    private String relationship;
}