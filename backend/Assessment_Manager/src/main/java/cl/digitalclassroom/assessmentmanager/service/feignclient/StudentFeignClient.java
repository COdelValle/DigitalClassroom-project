package cl.digitalclassroom.assessmentmanager.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", url = "${external.services.student-url}")
public interface StudentFeignClient {
    @GetMapping("/api/v1/students/{id}/profile")
    StudentProfileResponseDTO getStudentProfile(@PathVariable("id") Long id);
}
