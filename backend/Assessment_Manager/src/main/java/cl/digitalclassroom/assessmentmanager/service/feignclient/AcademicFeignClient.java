package cl.digitalclassroom.assessmentmanager.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "academic-service", url = "${external.services.academic-url}")
public interface AcademicFeignClient {

    // Verifica si la asignatura existe (ej: MAT-101)
    @GetMapping("/api/v1/subjects/{subjectId}/exists")
    Boolean verifySubject(@PathVariable("subjectId") String subjectId);

    // Verifica si la clase/curso existe (ej: 2-MEDIO-A)
    @GetMapping("/api/v1/classes/{classId}/exists")
    Boolean verifyClass(@PathVariable("classId") String classId);
}
