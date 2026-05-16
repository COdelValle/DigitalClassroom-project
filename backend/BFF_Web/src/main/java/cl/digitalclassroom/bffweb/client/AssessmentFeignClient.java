package cl.digitalclassroom.bffweb.client;

import cl.digitalclassroom.bffweb.dto.external.GradeResponseDTO;
import cl.digitalclassroom.bffweb.dto.request.GradeRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "assessment-service", url = "http://localhost:8083")
public interface AssessmentFeignClient {
    @PostMapping("/api/v1/grades")
    GradeResponseDTO saveGrade(@RequestBody GradeRequestDTO grade);

    @PutMapping("/api/v1/grades/{id}")
    GradeResponseDTO updateGrade(@PathVariable("id") Long id, @RequestBody GradeRequestDTO grade);

    @GetMapping("/api/v1/grades/search")
    List<GradeResponseDTO> searchGrades(
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "minScore", required = false) Double minScore,
            @RequestParam(value = "maxScore", required = false) Double maxScore,
            @RequestParam(value = "date", required = false) String date
    );
}
