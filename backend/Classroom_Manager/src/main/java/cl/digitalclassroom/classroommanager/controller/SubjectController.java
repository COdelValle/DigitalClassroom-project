package cl.digitalclassroom.classroommanager.controller;

import cl.digitalclassroom.classroommanager.model.dto.request.SubjectRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.SubjectModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.SubjectResponseDTO;
import cl.digitalclassroom.classroommanager.service.SubjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Asignaturas", description = "Operaciones CRUD para la gestión de asignaturas")
class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> subjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SubjectResponseDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String area
    ) {
        return ResponseEntity.ok(subjectService.searchSubjects(name, area));
    }

    @PostMapping
    public ResponseEntity<SubjectResponseDTO> post(@RequestBody SubjectRequestDTO request) {
        SubjectResponseDTO createdSubject = subjectService.createSubject(request);

        URI  location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSubject.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdSubject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> put(@PathVariable Long id, @RequestBody SubjectModifyRequestDTO request) {
        return ResponseEntity.ok(subjectService.updateSubject(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}
