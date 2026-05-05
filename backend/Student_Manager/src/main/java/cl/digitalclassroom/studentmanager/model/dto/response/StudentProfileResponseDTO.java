package cl.digitalclassroom.studentmanager.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para vista de perfil de estudiante (vista para docentes).
 * Contiene información esencial pero sin datos sensibles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponseDTO {

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

    /**
     * Lista de alergias del estudiante.
     */
    @Schema(example = "[\"Maní\", \"Camarones\"]", description = "Alergias del estudiante")
    private List<String> allergies;

    /**
     * Contactos de emergencia (representantes legales resumidos).
     */
    @Schema(description = "Contactos de emergencia")
    private List<EmergencyContactDTO> emergencyContacts;

    /**
     * DTO anidado para contactos de emergencia.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContactDTO {

        /**
         * Nombre del contacto de emergencia.
         */
        @Schema(example = "María García", description = "Nombre del contacto")
        private String name;

        /**
         * Números de teléfono del contacto.
         */
        @Schema(example = "[\"+56912345678\"]", description = "Números de teléfono")
        private List<String> phoneNumbers;

        /**
         * Relación con el estudiante.
         */
        @Schema(example = "Madre", description = "Relación con el estudiante")
        private String relationship;
    }
}

