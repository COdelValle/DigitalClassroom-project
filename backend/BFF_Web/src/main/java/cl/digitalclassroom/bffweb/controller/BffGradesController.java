package cl.digitalclassroom.bffweb.controller;

import cl.digitalclassroom.bffweb.dto.external.GradeResponseDTO;
import cl.digitalclassroom.bffweb.dto.request.GradeRequestDTO;
import cl.digitalclassroom.bffweb.dto.response.BffGradeSearchResponseDTO;
import cl.digitalclassroom.bffweb.dto.response.StudentReportCardDTO;
import cl.digitalclassroom.bffweb.service.BffGradesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bff/grades")
@RequiredArgsConstructor
public class BffGradesController {

    private final BffGradesService bffGradesService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentReportCardDTO> getStudentReportCard(@PathVariable Long studentId) {
        StudentReportCardDTO reportCard = bffGradesService.getStudentReportCard(studentId);

        // Si el Circuit Breaker activó el fallback, devolvemos un estado PARTIAL_CONTENT (206)
        if ("Módulo de notas en mantenimiento".equals(reportCard.getClassroomName())) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(reportCard);
        }
        return ResponseEntity.ok(reportCard);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BffGradeSearchResponseDTO>> searchBffGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @RequestParam(required = false) String date
    ) {
        return ResponseEntity.ok(bffGradesService.searchBffGrades(studentId, minScore, maxScore, date));
    }

    @PostMapping
    public ResponseEntity<GradeResponseDTO> addGrade(@RequestBody GradeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffGradesService.addGrade(request));
    }

    @PutMapping("/{gradeId}")
    public ResponseEntity<GradeResponseDTO> modifyGrade(
            @PathVariable Long gradeId,
            @RequestBody GradeRequestDTO request) {
        return ResponseEntity.ok(bffGradesService.modifyGrade(gradeId, request));
    }
}