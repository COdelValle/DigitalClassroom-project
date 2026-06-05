package cl.digitalclassroom.studentmanager.model.dto.response;

import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class StudentFullResponseDTO {
    private Long id;
    private String rut;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date birthDate;
    private List<String> allergies;
    private List<LegalRepresentativeDTO> legalRepresentatives;
}
