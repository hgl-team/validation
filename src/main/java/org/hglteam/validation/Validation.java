package org.hglteam.validation;

@FunctionalInterface
public interface Validation<T> {
    void validate(T target);

    static <T> MultistepValidationBuilder<T> builder() {
        return new MultistepValidationBuilder<>();
    }
}
