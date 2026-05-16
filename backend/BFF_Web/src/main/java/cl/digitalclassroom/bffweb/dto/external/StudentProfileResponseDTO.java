package cl.digitalclassroom.bffweb.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponseDTO {
    private Long id;
    private String rut;
    private String fullName;
    private List<String> allergies;
    private List<EmergencyContactDTO> emergencyContacts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContactDTO {
        private String name;
        private List<String> phoneNumbers;
        private String relationship;
    }
}