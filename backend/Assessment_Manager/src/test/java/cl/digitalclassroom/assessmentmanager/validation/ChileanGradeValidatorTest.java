package cl.digitalclassroom.assessmentmanager.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ChileanGradeValidatorTest {

    private ChileanGradeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ChileanGradeValidator();
        context = mock(ConstraintValidatorContext.class);
        validator.initialize(null);
    }

    @Test
    void shouldValidateScoreWithinRange() {
        assertThat(validator.isValid(1.0, context)).isTrue();
        assertThat(validator.isValid(4.5, context)).isTrue();
        assertThat(validator.isValid(7.0, context)).isTrue();
    }

    @Test
    void shouldRejectScoreOutsideRange() {
        assertThat(validator.isValid(0.9, context)).isFalse();
        assertThat(validator.isValid(7.1, context)).isFalse();
        assertThat(validator.isValid(null, context)).isFalse();
    }
}
