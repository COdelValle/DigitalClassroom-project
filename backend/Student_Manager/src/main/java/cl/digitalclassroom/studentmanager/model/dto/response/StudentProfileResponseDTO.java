package cl.digitalclassroom.studentmanager.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class StudentProfileResponseDTO {
    private Long id;
    private String rut;
    private String fullName;
    private List<String> allergies;
    private List<EmergencyContactDTO> emergencyContacts;

    @Data
    @Builder
    public static class EmergencyContactDTO {
        private String name;
        private List<String> phoneNumbers;
        private String relationship;
    }
}