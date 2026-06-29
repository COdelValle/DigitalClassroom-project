package cl.digitalclassroom.assessmentmanager.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChileanGradeValidatorRepeatTest {

    private ChileanGradeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ChileanGradeValidator();
        context = mock(ConstraintValidatorContext.class);
        validator.initialize(null);
    }

    @RepeatedTest(47)
    void repeatedValidateGradeInRange() {
        assertThat(validator.isValid(5.0, context)).isTrue();
    }
}
