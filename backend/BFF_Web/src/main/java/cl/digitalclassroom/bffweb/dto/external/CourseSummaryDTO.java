package cl.digitalclassroom.bffweb.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryDTO {
    private Long id;
    private String subjectName; // Ej: "Matemáticas"
    private String teacherName; // Ej: "Juan Pérez"
    private String semester;
}
