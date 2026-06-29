package cl.digitalclassroom.studentmanager.model.entity;

import cl.digitalclassroom.studentmanager.model.converter.LegalRepresentativeListConverter;
import cl.digitalclassroom.studentmanager.model.converter.StringListConverter;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.validation.RUT;
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
    private String rut;

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

    @Convert(converter = StringListConverter.class)
    @Column(nullable = true, length = 1000)
    @NotEmpty(message = "Debe registrar al menos una alergia o 'Ninguna'")
    private List<String> allergies;

    @Convert(converter = LegalRepresentativeListConverter.class)
    @Column(name = "legal_representatives", nullable = true, columnDefinition = "CLOB")
    @NotEmpty(message = "Tiene que haber al menos 1 representante")
    @Valid
    private List<LegalRepresentativeDTO> legalRepresentatives;
}
