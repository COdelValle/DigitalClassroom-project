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
    public boolean subjectExists(String subjectId) {
        return Boolean.TRUE.equals(academicFeignClient.verifySubject(subjectId));
    }

    // Validación de Clase
    @CircuitBreaker(name = "academicServiceCB", fallbackMethod = "fallbackAcademic")
    public boolean classExists(String classId) {
        return Boolean.TRUE.equals(academicFeignClient.verifyClass(classId));
    }

    // Fallback común para ambos
    private boolean fallbackAcademic(String id, Throwable e) {
        log.error("Circuit Breaker activo para el servicio Académico. No se pudo validar ID: {}. Error: {}",
                id, e.getMessage());
        throw new ServiceUnavailableException(
                "El servicio de validación académica no responde. No se puede procesar la solicitud en este momento."
        );
    }
}