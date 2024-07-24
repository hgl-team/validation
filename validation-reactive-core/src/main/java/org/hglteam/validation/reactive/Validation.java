package org.hglteam.validation.reactive;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Validation<T> {
    Mono<T> validate(T source);

    default Validation<T> then(Validation<T> validation) {
        return source -> this.validate(source)
                .flatMap(validation::validate);
    }
}
