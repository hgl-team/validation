package org.hglteam.validation.reactive;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ValidationError extends IllegalArgumentException {
    private ValidationError() { }

    public static <T extends RuntimeException> ValidationErrorBuilder<T> using(
            Function<String, T> exceptionConstructor) {
        return new ValidationErrorBuilder<>(exceptionConstructor);
    }

    public static <X, T extends RuntimeException> ExceptionProvider<X, T> from(
            Supplier<T> exceptionConstructor) {
        return x -> exceptionConstructor.get();
    }

    public static <X, T extends RuntimeException> ExceptionProvider<X, T> withMessage(
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

        public <X> ExceptionProvider<X, T> andNoArguments() {
            return x -> create(message);
        }

        public <X> ExceptionProvider<X, T> andArguments(Object... args) {
            return x -> create(message, args);
        }

        public <X> ExceptionProvider<X, T> andArguments(Function<X, Object[]> argumentExtractor) {
            return x -> create(message, argumentExtractor.apply(x));
        }

        @SafeVarargs
        public final <X> ExceptionProvider<X, T> andArguments(Function<X, Object>... argumentExtractors) {
            return x -> create(message, Arrays.stream(argumentExtractors)
                    .map(f -> f.apply(x))
                    .toArray());
        }

        private T create(String message, Object... args) {
            return (Objects.nonNull(message))
                    ? this.constructor.apply(MessageFormat.format(message, args))
                    : this.constructor.apply(null);
        }
    }
}
