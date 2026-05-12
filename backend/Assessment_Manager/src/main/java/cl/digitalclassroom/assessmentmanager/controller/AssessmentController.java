package cl.digitalclassroom.assessmentmanager.controller;

import cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentModifyRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.AssessmentResponseDTO;
import cl.digitalclassroom.assessmentmanager.service.AssessmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
@Tag(name = "Encargos", description = "Operaciones CRUD para la gestión de encargos")
class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponseDTO> assessmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.assessmentById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AssessmentResponseDTO>> search(
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Date examDate) {
        return ResponseEntity.ok(assessmentService.searchAssessments(subjectId, classId, title, examDate));
    }

    @PostMapping
    public ResponseEntity<AssessmentResponseDTO> post(@RequestBody AssessmentRequestDTO request) {
        AssessmentResponseDTO createdAssessment = assessmentService.createAssessment(request);

        // Creamos la URI del nuevo recurso (ej: /api/v1/assessments/5)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAssessment.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdAssessment);
    }

    @PutMapping
    public ResponseEntity<AssessmentResponseDTO> put(AssessmentModifyRequestDTO request) {
        return ResponseEntity.ok(assessmentService.updateAssessment(request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
    }
}
