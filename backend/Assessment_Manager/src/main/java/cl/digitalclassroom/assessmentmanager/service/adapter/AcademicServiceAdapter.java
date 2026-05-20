package cl.digitalclassroom.assessmentmanager.service.adapter;

import cl.digitalclassroom.assessmentmanager.exception.ServiceUnavailableException;
import cl.digitalclassroom.assessmentmanager.service.feignclient.AcademicFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicServiceAdapter {
    private final AcademicFeignClient academicFeignClient;

    // Validación de Asignatura
    @CircuitBreaker(name = "academicServiceCB", fallbackMethod = "fallbackAcademic")
    public boolean courseExists(Long courseId) {
        return Boolean.TRUE.equals(academicFeignClient.verifyClass(courseId));
    }

    // Fallback común para ambos
    @SuppressWarnings("unused")
    private boolean fallbackAcademic(Long courseId, Throwable e) {
        log.error("Circuit Breaker activo para el servicio Académico. No se pudo validar courseId: {}. Error: {}",
                courseId, e.getMessage());
        throw new ServiceUnavailableException(
                "El servicio de validación académica no responde. No se puede procesar la solicitud en este momento."
        );
    }
}