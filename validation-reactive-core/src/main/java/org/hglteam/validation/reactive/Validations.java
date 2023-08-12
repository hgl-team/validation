package org.hglteam.validation.reactive;

public final class Validations {

    public static <T> MultiStepValidation<T> builder() {
        return new MultiStepValidation<>();
    }

    public static <T> Validation<T> from(
            ReactivePredicate<T> reactivePredicate,
            ExceptionProvider<T, ?> exceptionProvider) {
        return SimpleValidation.<T>builder()
                .predicate(reactivePredicate)
                .exceptionProvider(exceptionProvider)
                .build();
    }

    private Validations() { }
}
