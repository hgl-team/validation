package org.hglteam.validation.reactive;

import reactor.core.publisher.Mono;

public class MultiStepValidation<T>
        extends MultiStepValidationBuilderBase<T, MultiStepValidation<T>>
        implements Validation<T> {
    @Override
    protected MultiStepValidation<T> self() {
        return this;
    }

    @Override
    public Mono<T> validate(T source) {
        return this.getFinalValidation().validate(source);
    }
}
