package cl.digitalclassroom.bffweb.client;

import cl.digitalclassroom.bffweb.dto.external.StudentProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", url = "http://localhost:8081")
public interface StudentFeignClient {
    @GetMapping("/api/v1/students/{id}/profile")
    StudentProfileResponseDTO getStudentById(@PathVariable("id") Long id);
}
