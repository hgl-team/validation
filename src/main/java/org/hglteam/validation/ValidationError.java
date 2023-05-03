package org.hglteam.validation;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ValidationError extends IllegalArgumentException {
    private ValidationError() { }

    public static <T extends RuntimeException> ValidationErrorBuilder<T> using(
            Function<String, T> exceptionConstructor) {
        return new ValidationErrorBuilder<>(exceptionConstructor);
    }

    public static <X, T extends RuntimeException> Function<X, T> from(
            Supplier<T> exceptionConstructor) {
        return x -> exceptionConstructor.get();
    }

    public static <X, T extends RuntimeException> Function<X, T> withMessage(
            Function<String, T> constructor,
            String message,
            Object... args) {
        return using(constructor).message(message).andArguments(args);
    }

    public static class ValidationErrorBuilder<T extends RuntimeException> {
        private final Function<String, T> constructor;
        private String message;

        public ValidationErrorBuilder(Function<String, T> constructor) {
            this.constructor = constructor;
        }

        public ValidationErrorBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public <X> Function<X, T> andNoArguments() {
            return andArguments();
        }

        public <X> Function<X, T> andArguments(Object... args) {
            return andArguments(x -> args);
        }

        public <X> Function<X, T> andArguments(Function<X, Object[]> argumentExtractor) {
            return x -> create(message, argumentExtractor.apply(x));
        }

        private T create(String message, Object[] args) {
            return (Objects.nonNull(message))
                    ? this.constructor.apply(MessageFormat.format(message, args))
                    : this.constructor.apply(null);
        }
    }
}
