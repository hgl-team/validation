package org.hglteam.validation;

public class MultistepValidationBuilder<T>
        extends MultistepValidationBuilderBase<T, MultistepValidationBuilder<T>>
        implements Validation<T> {

    MultistepValidationBuilder() { }

    @Override
    protected MultistepValidationBuilder<T> self() {
        return this;
    }

    @Override
    public void validate(T target) {
        this.validations.forEach(v -> v.validate(target));
    }
}
