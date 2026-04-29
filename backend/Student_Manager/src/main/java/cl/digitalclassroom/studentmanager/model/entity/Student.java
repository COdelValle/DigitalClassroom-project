package cl.digitalclassroom.studentmanager.model.entity;

import cl.digitalclassroom.studentmanager.Validation.RUT;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @NotBlank(message = "Se requiere ingresar RUT")
    @RUT(message = "RUT invalido")
    @Column(unique = true, nullable = false)
    private String RUT;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 2, max = 50, message = "El nombre tiene que tener entre 2 a 50 caracteres")
    @Column(nullable = false)
    private String firstName;

    @Size(max = 50, message = "El segundo nombre puede tener máximo 50 caracteres")
    @Column(nullable = true)
    private String middleName;

    @NotBlank(message = "El/los apellido/s no puede/n estar vacio")
    @Size(min = 2, max = 100, message = "El/los apellido/s tiene/n que tener entre 2 a 100 caracteres")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Column(nullable = false)
    private Date birthDate;

    @NotEmpty(message = "Debe registrar al menos una alergia o 'Ninguna'")
    @Column(nullable = true)
    private List<String> allergies;

    @Size(min = 1, message = "Tiene que haber 1 representante mínimo")
    @Valid
    @Column(nullable = false)
    private List<LegalRepresentativeDTO> legalRepresentatives;
}
