package org.hglteam.validation.reactive;

import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class MultiStepValidationBuilderBase<T, B extends MultiStepValidationBuilderBase<T, B>> {
    private Validation<T> finalValidation;

    protected MultiStepValidationBuilderBase() {
        this.finalValidation = Mono::just;
    }

    public ValidationStepBuilder when(ReactivePredicate<T> predicate) {
        return new ValidationStepBuilder(predicate);
    }

    public ValidationStepBuilder whenValue(Predicate<T> predicate) {
        return when(ReactivePredicate.of(predicate));
    }

    public <U> B onProperty(Function<T, U> getter, Consumer<MultiStepValidation<U>> validator) {
        var propertyValidator = Validations.<U>builder();

        validator.accept(propertyValidator);

        return this.thenCheck(source -> Mono.just(source)
                .map(getter)
                .flatMap(propertyValidator::validate)
                .then(Mono.just(source)));
    }

    protected Validation<T> getFinalValidation() {
        return finalValidation;
    }

    protected abstract B self();

    public B thenCheck(Validation<T> validation) {
        this.finalValidation = finalValidation.then(validation);
        return self();
    }

    public class ValidationStepBuilder {
        public ReactivePredicate<T> predicate;

        public ValidationStepBuilder(ReactivePredicate<T> predicate) {
            this.predicate = predicate;
        }

        public ValidationStepBuilder and(ReactivePredicate<T> other) {
            predicate = predicate.and(other);
            return this;
        }

        public ValidationStepBuilder andValue(Predicate<T> other) {
            return and(ReactivePredicate.of(other));
        }

        public ValidationStepBuilder or(ReactivePredicate<T> other) {
            predicate = predicate.or(other);
            return this;
        }

        public ValidationStepBuilder orValue(Predicate<T> other) {
            return or(ReactivePredicate.of(other));
        }

        public B then(ExceptionProvider<T, ?> exceptionProvider) {
            return MultiStepValidationBuilderBase.this.thenCheck(SimpleValidation.<T>builder()
                    .predicate(predicate)
                    .exceptionProvider(exceptionProvider)
                    .build());
        }
    }
}
