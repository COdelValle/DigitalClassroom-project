package cl.digitalclassroom.classroommanager.controller;

import cl.digitalclassroom.classroommanager.model.dto.request.ClassroomRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.ClassroomModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.ClassroomResponseDTO;
import cl.digitalclassroom.classroommanager.service.ClassroomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/classroom")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Operaciones CRUD para la gestión de cursos")
class ClassroomController {
    private final ClassroomService classroomService;

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponseDTO> classroomById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClassroomResponseDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(classroomService.searchClassrooms(name, year));
    }

    @PostMapping
    public ResponseEntity<ClassroomResponseDTO> post(@RequestBody ClassroomRequestDTO request) {
        ClassroomResponseDTO createdClassroom = classroomService.createClassroom(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdClassroom.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdClassroom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResponseDTO> put(@PathVariable Long id, @RequestBody ClassroomModifyRequestDTO request) {
        return ResponseEntity.ok(classroomService.updateClassroom(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
    }
}
