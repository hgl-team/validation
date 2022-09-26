package org.hglteam.validation;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.validation.ValidationException;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@SuperBuilder(toBuilder = true)
public class SimpleValidation<T> implements Validation<T> {
    private final Predicate<T> predicate;
    private final Function<T, ValidationException> exceptionFunction;

    @Override
    public void validate(T target) {
        if(predicate.test(target)) {
            throw exceptionFunction.apply(target);
        }
    }
}
