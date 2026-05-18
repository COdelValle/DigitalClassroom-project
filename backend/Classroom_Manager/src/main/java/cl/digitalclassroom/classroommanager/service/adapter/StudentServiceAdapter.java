package cl.digitalclassroom.classroommanager.service.adapter;

import cl.digitalclassroom.classroommanager.exception.ServiceUnavailableException;
import cl.digitalclassroom.classroommanager.service.feignclient.StudentFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentServiceAdapter {

    private final StudentFeignClient studentFeignClient;

    @CircuitBreaker(name = "studentServiceCB", fallbackMethod = "fallbackStudentValidation")
    public boolean studentExists(Long studentId) {
        log.info("Validando existencia del estudiante ID: {} vía Feign", studentId);
        // Feign lanzará una excepción si recibe un 404 o un 500
        var response = studentFeignClient.exists(studentId);
        return response != false;
    }

    // FALLBACK: Se ejecuta si el microservicio de Estudiantes está caído
    // o si el circuito se abrió por demasiados errores.
    @SuppressWarnings("unused")
    private boolean fallbackStudentValidation(Long studentId, Throwable e) {
        log.error("Circuit Breaker ACTIVADO. No se pudo validar al estudiante {}. Motivo: {}",
                studentId, e.getMessage());
        throw new ServiceUnavailableException(
                "El servicio de validación estudiantil no responde. No se puede procesar la solicitud en este momento."
        );
    }
}
