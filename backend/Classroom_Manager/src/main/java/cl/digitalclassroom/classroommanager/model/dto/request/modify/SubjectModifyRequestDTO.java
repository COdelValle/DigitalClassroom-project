package cl.digitalclassroom.classroommanager.model.dto.request.modify;

import lombok.Data;

@Data
public class SubjectModifyRequestDTO {
    private Long id;
    private String name;
    private String area;
}
