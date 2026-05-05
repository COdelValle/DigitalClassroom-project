package cl.digitalclassroom.studentmanager.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta simplificada para el listado de estudiantes en tablas.
 * Contiene solo información básica para optimizar rendimiento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentShortResponseDTO {

    /**
     * Identificador del estudiante.
     */
    @Schema(example = "1", description = "ID del estudiante")
    private Long id;

    /**
     * RUT del estudiante.
     */
    @Schema(example = "12.345.678-9", description = "RUT del estudiante")
    private String rut;

    /**
     * Nombre completo del estudiante.
     */
    @Schema(example = "Juan García López", description = "Nombre completo del estudiante")
    private String fullName;
}

