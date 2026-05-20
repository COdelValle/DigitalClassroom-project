package cl.digitalclassroom.bffweb.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentShortResponseDTO {
    private Long id;
    private String rut;
    private String fullName;
}