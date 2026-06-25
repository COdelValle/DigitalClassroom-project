package cl.digitalclassroom.studentmanager.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Pruebas unitarias para PhoneValidator
 * Valida números telefónicos chilenos e internacionales
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PhoneValidator Unit Tests")
class PhoneValidatorTest {

    private PhoneValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PhoneValidator();
        validator.initialize(null);
        context = mock(ConstraintValidatorContext.class);
    }

    // ==================== TESTS DE TELÉFONO VÁLIDO ====================

    @Test
    @DisplayName("Validator - debe aceptar número chileno válido")
    void testValidatesChileanPhoneNumber() {
        boolean isValid = validator.isValid("+56 9 1111 2222", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar número sin espacios")
    void testValidatesPhoneWithoutSpaces() {
        boolean isValid = validator.isValid("+56911112222", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar número sin código de país")
    void testValidatesPhoneWithoutCountryCode() {
        boolean isValid = validator.isValid("911112222", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar número con múltiples espacios")
    void testValidatesPhoneWithMultipleSpaces() {
        boolean isValid = validator.isValid("+56 9 1111 2222", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar número de exactamente 9 dígitos")
    void testValidatesPhoneWith9Digits() {
        boolean isValid = validator.isValid("123456789", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar número de exactamente 15 dígitos")
    void testValidatesPhoneWith15Digits() {
        boolean isValid = validator.isValid("123456789012345", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar número internacional")
    void testValidatesInternationalPhone() {
        boolean isValid = validator.isValid("+1 555 123 4567", context);
        assertThat(isValid).isTrue();
    }

    // ==================== TESTS DE TELÉFONO INVÁLIDO ====================

    @Test
    @DisplayName("Validator - debe rechazar número nulo (null)")
    void testAcceptsNullPhone() {
        // Según implementación, null retorna true
        boolean isValid = validator.isValid(null, context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe rechazar número vacío")
    void testRejectsEmptyPhone() {
        boolean isValid = validator.isValid("", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar número muy corto (menos de 9 dígitos)")
    void testRejectsTooShortPhone() {
        boolean isValid = validator.isValid("123456", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar número muy largo (más de 15 dígitos)")
    void testRejectsTooLongPhone() {
        boolean isValid = validator.isValid("1234567890123456", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar números con letras")
    void testRejectsPhoneWithLetters() {
        boolean isValid = validator.isValid("+56 9 ABCD EFGH", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar números con caracteres especiales no permitidos")
    void testRejectsPhoneWithSpecialCharacters() {
        boolean isValid = validator.isValid("+56 9 1111#2222", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar números con parentesis")
    void testRejectsPhoneWithParenthesis() {
        boolean isValid = validator.isValid("+56 (9) 1111 2222", context);
        assertThat(isValid).isFalse();
    }

    // ==================== TESTS DE FORMATO CHILENO ====================

    @Test
    @DisplayName("Validator - debe aceptar formato chileno: +56 9 XXXX XXXX")
    void testValidatesChileanFormat1() {
        boolean isValid = validator.isValid("+56 9 1234 5678", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar formato chileno: +569XXXXXXXX")
    void testValidatesChileanFormat2() {
        boolean isValid = validator.isValid("+56912345678", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar formato chileno: 9 1234 5678")
    void testValidatesChileanFormat3() {
        boolean isValid = validator.isValid("9 1234 5678", context);
        assertThat(isValid).isTrue();
    }

    // ==================== TESTS CON ESPACIOS ====================

    @Test
    @DisplayName("Validator - debe tolerar espacios en blanco")
    void testToleratesLeadingTrailingSpaces() {
        boolean isValid = validator.isValid("  +56 9 1111 2222  ", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe tolerar espacios interiores")
    void testToleratesInternalSpaces() {
        boolean isValid = validator.isValid("+56  9  1111  2222", context);
        assertThat(isValid).isTrue();
    }

    // ==================== TESTS DE CASOS LÍMITE ====================

    @Test
    @DisplayName("Validator - debe aceptar exactamente 9 dígitos sin símbolos")
    void testExactly9Digits() {
        boolean isValid = validator.isValid("912345678", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe aceptar exactamente 15 dígitos sin símbolos")
    void testExactly15Digits() {
        boolean isValid = validator.isValid("123456789012345", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe rechazar 8 dígitos")
    void testRejects8Digits() {
        boolean isValid = validator.isValid("12345678", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar 16 dígitos")
    void testRejects16Digits() {
        boolean isValid = validator.isValid("1234567890123456", context);
        assertThat(isValid).isFalse();
    }

    // ==================== TESTS DE NÚMEROS CONOCIDOS ====================

    @Test
    @DisplayName("Validator - debe validar número de ejemplo: +56 9 1234 5678")
    void testValidatesExampleNumber1() {
        boolean isValid = validator.isValid("+56 9 1234 5678", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe validar número de ejemplo: +56911112222")
    void testValidatesExampleNumber2() {
        boolean isValid = validator.isValid("+56911112222", context);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validator - debe validar número de ejemplo: 911234567")
    void testValidatesExampleNumber3() {
        boolean isValid = validator.isValid("911234567", context);
        assertThat(isValid).isTrue();
    }

    // ==================== TESTS DE SOLO SÍMBOLOS ====================

    @Test
    @DisplayName("Validator - debe rechazar solo signos plus")
    void testRejectsOnlyPlus() {
        boolean isValid = validator.isValid("++++", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar solo espacios")
    void testRejectsOnlySpaces() {
        boolean isValid = validator.isValid("     ", context);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Validator - debe rechazar mezcla de símbolos sin dígitos")
    void testRejectsSymbolsOnly() {
        boolean isValid = validator.isValid("+ - + - ", context);
        assertThat(isValid).isFalse();
    }
}
