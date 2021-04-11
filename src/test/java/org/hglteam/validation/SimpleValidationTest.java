package org.hglteam.validation;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class SimpleValidationTest {

    @Test
    void validationPass() {
        var validation = SimpleValidation.builder()
                .predicate(Objects::nonNull)
                .exceptionFunction(ValidationException.withMessage("Should pass!!"))
                .build();

        assertDoesNotThrow(() -> validation.validate(null));
    }

    @Test
    void validationError() {
        var validation = SimpleValidation.builder()
                .predicate(Objects::isNull)
                .exceptionFunction(ValidationException.withMessage("Should not pass!!"))
                .build();

        assertThrows(ValidationException.class, () -> validation.validate(null));
    }
}
