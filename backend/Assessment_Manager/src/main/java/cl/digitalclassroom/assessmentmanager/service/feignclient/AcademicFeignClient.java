package cl.digitalclassroom.assessmentmanager.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "academic-service", url = "${external.services.academic-url}")
public interface AcademicFeignClient {

    @GetMapping("/api/v1/courses/{courseId}/exists")
    Boolean verifyClass(@PathVariable("courseId") Long courseId);
}
