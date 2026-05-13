package cl.digitalclassroom.assessmentmanager.model.dto.response;

import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GradeResponseDTO {
    private Long id;
    private Long studentId;
    private Double score;
    private LocalDateTime registrationDate;

    public static GradeResponseDTO fromEntity(Grade entity) {
        return GradeResponseDTO.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .score(entity.getScore())
                .registrationDate(entity.getRegistrationDate())
                .build();
    }
}
