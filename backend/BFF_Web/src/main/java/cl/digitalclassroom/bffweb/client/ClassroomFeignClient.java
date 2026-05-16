package cl.digitalclassroom.bffweb.client;

import cl.digitalclassroom.bffweb.dto.external.ClassroomResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.CourseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "classroom-service", url = "http://localhost:8084")
public interface ClassroomFeignClient {

    @GetMapping("/api/v1/classroom/{id}")
    ClassroomResponseDTO getClassroomById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/classroom/search")
    List<ClassroomResponseDTO> searchClassrooms(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "year", required = false) Integer year
    );

    @GetMapping("/api/v1/courses/search")
    List<CourseResponseDTO> searchCourses(
            @RequestParam(value = "classroomId", required = false) Long classroomId,
            @RequestParam(value = "teacher", required = false) String teacher,
            @RequestParam(value = "semester", required = false) String semester
    );
}
