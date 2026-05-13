package cl.digitalclassroom.assessmentmanager.model.dto.response;

import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AssessmentResponseDTO {
    private Long id;
    private String title;
    private String subjectId;
    private String classId;
    private LocalDate examDate;
    private boolean isGraded;
    private List<GradeResponseDTO> grades;

    public static AssessmentResponseDTO fromEntity(Assessment entity) {
        return AssessmentResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .subjectId(entity.getSubjectId())
                .classId(entity.getClassId())
                .examDate(entity.getExamDate())
                .isGraded(entity.isGraded())
                .grades(entity.getGrades() != null ?
                        entity.getGrades().stream().map(GradeResponseDTO::fromEntity).toList() :
                        new ArrayList<>())
                .build();
    }
}
