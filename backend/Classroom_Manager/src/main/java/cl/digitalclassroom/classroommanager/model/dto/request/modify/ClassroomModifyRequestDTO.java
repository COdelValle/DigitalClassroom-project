package cl.digitalclassroom.classroommanager.model.dto.request.modify;

import lombok.Data;

import java.util.List;

@Data
public class ClassroomModifyRequestDTO {
    private Long id;
    private String name;
    private List<Long> studentIds;
}
