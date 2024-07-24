package org.hglteam.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class MultistepValidationBuilderBase<T, B extends MultistepValidationBuilderBase<T, B>> {
    protected final List<Validation<T>> validations;

    protected MultistepValidationBuilderBase() {
        validations = new ArrayList<>();
    }

    public ValidationStepBuilder when(Predicate<T> predicate) {
        return new ValidationStepBuilder(predicate);
    }

    public B thenCheck(Validation<T> other) {
        validations.add(other);
        return self();
    }

    public <P> B onProperty(Function<T, P> getter, Consumer<MultistepValidationBuilderBase<P, ?>> builderConfigurator) {
        this.validations.add(this.propertyValidator(getter, builderConfigurator));
        return self();
    }

    protected <P> Validation<T> propertyValidator(Function<T, P> getter, Consumer<MultistepValidationBuilderBase<P, ?>> builderConfigurator) {
        return t -> {
            var value = getter.apply(t);
            var builder = Validation.<P>builder();

            builderConfigurator.accept(builder);

            builder.validate(value);
        };
    }

    protected abstract B self();

    public class ValidationStepBuilder {
        private Predicate<T> predicate;

        private ValidationStepBuilder(Predicate<T> condition) {
            this.predicate = condition;
        }

        public ValidationStepBuilder and(Predicate<T> condition) {
            this.predicate = this.predicate.and(condition);
            return this;
        }

        public ValidationStepBuilder or(Predicate<T> condition) {
            this.predicate = this.predicate.or(condition);
            return this;
        }

        public B then(Function<T, ? extends RuntimeException> exceptionFunction) {
            MultistepValidationBuilderBase.this
                    .validations
                    .add(SimpleValidation.<T>builder()
                            .predicate(this.predicate)
                            .exceptionFunction(exceptionFunction)
                            .build());
            return MultistepValidationBuilderBase.this.self();
        }
    }
}
