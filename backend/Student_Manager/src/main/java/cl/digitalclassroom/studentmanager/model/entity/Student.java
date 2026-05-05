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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Entidad que representa un Estudiante en el sistema.
 * Almacena información personal, de contacto y de representantes legales.
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students", indexes = {
    @Index(name = "idx_rut", columnList = "rut", unique = true),
    @Index(name = "idx_birth_date", columnList = "birth_date")
})
public class Student {

    /**
     * Identificador único del estudiante (PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RUT del estudiante. Único y obligatorio. Validado con algoritmo chileno.
     */
    @NotBlank(message = "Se requiere ingresar RUT")
    @RUT(message = "RUT invalido")
    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    /**
     * Primer nombre del estudiante.
     */
    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 2, max = 50, message = "El nombre tiene que tener entre 2 a 50 caracteres")
    @Column(nullable = false, length = 50)
    private String firstName;

    /**
     * Segundo nombre del estudiante (opcional).
     */
    @Size(max = 50, message = "El segundo nombre puede tener máximo 50 caracteres")
    @Column(nullable = true, length = 50)
    private String middleName;

    /**
     * Apellidos del estudiante.
     */
    @NotBlank(message = "El/los apellido/s no puede/n estar vacio")
    @Size(min = 2, max = 100, message = "El/los apellido/s tiene/n que tener entre 2 a 100 caracteres")
    @Column(nullable = false, length = 100)
    private String lastName;

    /**
     * Fecha de nacimiento del estudiante.
     */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Column(nullable = false)
    private Date birthDate;

    /**
     * Lista de alergias del estudiante. Al menos una entrada es requerida.
     */
    @NotEmpty(message = "Debe registrar al menos una alergia o 'Ninguna'")
    @Column(columnDefinition = "JSON", nullable = false)
    private List<String> allergies;

    /**
     * Lista de representantes legales del estudiante.
     * Al menos uno es obligatorio.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_representatives",
            joinColumns = @JoinColumn(name = "student_id", nullable = false)
    )
    @NotEmpty(message = "Tiene que haber al menos 1 representante")
    @Valid
    private List<LegalRepresentativeDTO> legalRepresentatives;

    /**
     * Fecha y hora de creación del registro. Se establece automáticamente.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización. Se actualiza automáticamente.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
