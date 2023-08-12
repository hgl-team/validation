package org.hglteam.validation.reactive;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import reactor.core.publisher.Mono;

@Getter
@SuperBuilder(toBuilder = true)
public class SimpleValidation<T> implements Validation<T> {
    private final ReactivePredicate<T> predicate;
    private final ExceptionProvider<T, ?> exceptionProvider;

    @Override
    public Mono<T> validate(T source) {
        return predicate.test(source)
                .flatMap(result -> result
                        ? Mono.error(exceptionProvider.create(source))
                        : Mono.just(source));
    }
}
