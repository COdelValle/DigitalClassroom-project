package cl.digitalclassroom.bffweb.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponseDTO {
    private Long id;
    private Long studentId;
    private Double score;
    private LocalDateTime registrationDate;
}
