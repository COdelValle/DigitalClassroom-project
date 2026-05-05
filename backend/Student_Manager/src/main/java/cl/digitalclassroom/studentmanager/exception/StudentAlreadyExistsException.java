package cl.digitalclassroom.studentmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta crear un estudiante con un RUT que ya existe en la base de datos.
 * Retorna un estado HTTP 409 (Conflict).
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class StudentAlreadyExistsException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message Mensaje de error personalizado
     */
    public StudentAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje de error personalizado
     * @param cause Causa raíz de la excepción
     */
    public StudentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

