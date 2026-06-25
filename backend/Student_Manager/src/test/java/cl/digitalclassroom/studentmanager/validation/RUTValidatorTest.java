package cl.digitalclassroom.studentmanager.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Pruebas unitarias para RUTValidator
 * Valida el algoritmo de validación de RUT chileno
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RUTValidator Unit Tests")
class RUTValidatorTest {

    private RUTValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new RUTValidator();
        validator.initialize(null);
        context = mock(ConstraintValidatorContext.class);
    }

    // ==================== TESTS DE RUT VÁLIDO ====================

    @Test
    @DisplayName("Validator - debe aceptar RUT válido con formato completo")
    void testValidatesValidRutWithFormat() {
        boolean isValid = validator.isValid("22.126.386-3", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar RUT válido sin puntos")
    void testValidatesValidRutWithoutDots() {
        boolean isValid = validator.isValid("221263863", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar RUT con DV K")
    void testValidatesRutWithKCheckDigit() {
        boolean isValid = validator.isValid("25.000.009-K", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar RUT en minúsculas con K")
    void testValidatesRutWithLowercaseK() {
        boolean isValid = validator.isValid("25.000.009-k", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar RUT sin guión")
    void testValidatesRutWithoutHyphen() {
        boolean isValid = validator.isValid("22.126.3863", context);
        assertThat(isValid).isTrue();
    }

    // ==================== TESTS DE RUT INVÁLIDO ====================

    @Test
    @DisplayName("Validator - debe rechazar RUT nulo")
    void testRejectsNullRut() {
        boolean isValid = validator.isValid(null, context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT vacío")
    void testRejectsEmptyRut() {
        boolean isValid = validator.isValid("", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT con dígito verificador incorrecto")
    void testRejectsInvalidCheckDigit() {
        boolean isValid = validator.isValid("22.126.386-0", context);  // DV debería ser 3
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT muy corto")
    void testRejectsTooShortRut() {
        boolean isValid = validator.isValid("1-9", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT con caracteres no numéricos (excepto formato)")
    void testRejectsRutWithLetters() {
        boolean isValid = validator.isValid("12.ABC.678-9", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT con caracteres especiales")
    void testRejectsRutWithSpecialCharacters() {
        boolean isValid = validator.isValid("12@345@678-9", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT malformado")
    void testRejectsMalformedRut() {
        boolean isValid = validator.isValid("..123..456..-9", context);
        assertThat(isValid).isFalse();
    }

    // ==================== TESTS DE RUTS CONOCIDOS ====================

    @Test
    @DisplayName("Validator - debe validar RUT conocido: 22.126.386-3")
    void testValidatesKnownRut1() {
        boolean isValid = validator.isValid("22.126.386-3", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe validar RUT conocido: 22.009.301-8")
    void testValidatesKnownRut2() {
        boolean isValid = validator.isValid("22.009.301-8", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT con dígito verificador equivocado")
    void testRejectsWrongCheckDigit() {
        boolean isValid = validator.isValid("8.449.534-1", context);  // DV debería ser K
        assertThat(isValid).isFalse();
    }

    // ==================== TESTS DE ESPACIOS EN BLANCO ====================

    @Test
    @DisplayName("Validator - debe tolerar espacios en blanco")
    void testToleratesWhitespace() {
        boolean isValid = validator.isValid("  22.126.386-3  ", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe rechazar RUT solo con espacios")
    void testRejectsOnlyWhitespace() {
        boolean isValid = validator.isValid("   ", context);
        assertThat(isValid).isFalse();
    }

    // ==================== TESTS DE CASOS LÍMITE ====================

    @Test
    @DisplayName("Validator - debe validar RUT mínimo válido")
    void testValidatesMinimalValidRut() {
        // El RUT mínimo típicamente es 1.000.000+
        boolean isValid = validator.isValid("1.000.000-9", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe validar RUT máximo")
    void testValidatesMaximalRut() {
        boolean isValid = validator.isValid("99.999.999-9", context);
        assertThat(isValid).isTrue();
    }
}
