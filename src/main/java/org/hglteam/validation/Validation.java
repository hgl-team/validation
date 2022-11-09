package org.hglteam.validation;

@FunctionalInterface
public interface Validation<T> {
    void validate(T target);

    default T valid(T target) {
        validate(target);
        return target;
    }

    static <T> MultistepValidationBuilder<T> builder() {
        return new MultistepValidationBuilder<>();
    }
}
