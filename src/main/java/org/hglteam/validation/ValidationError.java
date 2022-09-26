package org.hglteam.validation;

import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ValidationError extends IllegalArgumentException {
    private ValidationError() { }

    public static <X> Function<X, ValidationException> withMessage(String message, Object... args) {
        return t -> new ValidationException(MessageFormat.format(message,
                Stream.concat(Stream.of(t), Arrays.stream(args)).toArray()));
    }
}
