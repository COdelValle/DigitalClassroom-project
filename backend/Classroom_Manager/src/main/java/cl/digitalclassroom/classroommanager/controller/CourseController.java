package cl.digitalclassroom.classroommanager.controller;

import cl.digitalclassroom.classroommanager.model.dto.request.CourseRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.CourseModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.CourseResponseDTO;
import cl.digitalclassroom.classroommanager.service.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Relación entre Cursos y asignaturas", description = "Operaciones CRUD para la gestión de registros de asignaturas asignadas por cursos")
class CourseController {
    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> courseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDTO>> search(
            @RequestParam(required = false) Long classroomId,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String semester
    ) {
        return ResponseEntity.ok(courseService.searchCourses(classroomId, teacher, semester));
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> post(@RequestBody CourseRequestDTO request) {
        CourseResponseDTO createdCourse = courseService.createCourse(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCourse.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> put(@PathVariable Long id, @RequestBody CourseModifyRequestDTO request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
