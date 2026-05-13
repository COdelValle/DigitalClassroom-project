package cl.digitalclassroom.assessmentmanager.controller;

import cl.digitalclassroom.assessmentmanager.model.dto.request.GradeRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.modify.GradeModifyRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.GradeResponseDTO;
import cl.digitalclassroom.assessmentmanager.model.entity.SLA;
import cl.digitalclassroom.assessmentmanager.service.GradeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Operaciones CRUD para la gestión de notas")
class GradeController {
    private final GradeService gradeService;

    @GetMapping("/{id}")
    public ResponseEntity<GradeResponseDTO> gradeById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.gradeById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GradeResponseDTO>> search(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @RequestParam(required = false) LocalDate date
    ) {
        SLA sla = new SLA(LocalDate.now(),null, "GET /search", null);
        List<GradeResponseDTO> dtos = gradeService.searchGrades(studentId, minScore, maxScore, date);
        sla.setFechaFinal(LocalDate.now());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<GradeResponseDTO> post(@RequestBody GradeRequestDTO request) {
        GradeResponseDTO createdGrade = gradeService.saveIndependentGrade(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdGrade.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdGrade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeResponseDTO> put(@PathVariable Long id, @RequestBody GradeModifyRequestDTO request) {
        return ResponseEntity.ok(gradeService.updateGrade(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        gradeService.deleteGrade(id);
    }
}
