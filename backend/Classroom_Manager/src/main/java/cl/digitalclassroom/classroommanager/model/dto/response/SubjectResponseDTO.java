package cl.digitalclassroom.classroommanager.model.dto.response;

import cl.digitalclassroom.classroommanager.model.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDTO {
    private Long id;
    private String name;
    private String area;
    private Boolean isActive;

    public static SubjectResponseDTO fromEntity(Subject subject) {
        return SubjectResponseDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .area(subject.getArea())
                .isActive(subject.getIsActive())
                .build();
    }
}
