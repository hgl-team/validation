package org.hglteam.validation;

import org.junit.jupiter.api.Test;

import javax.validation.ValidationException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class SimpleValidationTest {

    @Test
    void validationPass() {
        var validation = SimpleValidation.builder()
                .predicate(Objects::nonNull)
                .exceptionFunction(ValidationError.withMessage("Should pass!!"))
                .build();

        assertDoesNotThrow(() -> validation.validate(null));
    }

    @Test
    void validationError() {
        var validation = SimpleValidation.builder()
                .predicate(Objects::isNull)
                .exceptionFunction(ValidationError.withMessage("Should not pass!!"))
                .build();

        assertThrows(ValidationException.class, () -> validation.validate(null));
    }

    @Test
    void validationPassUsingValidMethod() {
        var validation = SimpleValidation.<Integer>builder()
                .predicate(Objects::isNull)
                .exceptionFunction(ValidationError.withMessage("Should pass!!"))
                .build();

        assertDoesNotThrow(() -> {
            var value = validation.valid(10);
            assertEquals(10, value);
        });
    }

    @Test
    void validationErrorUsingValidMethod() {
        var validation = SimpleValidation.builder()
                .predicate(Objects::isNull)
                .exceptionFunction(ValidationError.withMessage("Should not pass!!"))
                .build();

        assertThrows(ValidationException.class, () -> validation.valid(null));
    }
}
