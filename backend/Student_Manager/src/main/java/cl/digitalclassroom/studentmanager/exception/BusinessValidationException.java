package cl.digitalclassroom.studentmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando hay un error de validación de negocio.
 * Retorna un estado HTTP 400 (Bad Request).
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message Mensaje de error personalizado
     */
    public BusinessValidationException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje de error personalizado
     * @param cause Causa raíz de la excepción
     */
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

