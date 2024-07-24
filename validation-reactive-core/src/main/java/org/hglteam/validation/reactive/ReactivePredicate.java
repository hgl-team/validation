package org.hglteam.validation.reactive;

import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@FunctionalInterface
public interface ReactivePredicate<T> {
    Mono<Boolean> test(T value);

    default ReactivePredicate<T> and(ReactivePredicate<T> other) {
        return value -> this.test(value)
                .flatMap(result -> result ? other.test(value) : Mono.just(false));
    }

    default ReactivePredicate<T> or(ReactivePredicate<T> other) {
        return value -> this.test(value)
                .flatMap(result -> result ? Mono.just(true) : other.test(value));
    }

    static <T> ReactivePredicate<T> of(Predicate<T> predicate) {
        return value -> Mono.just(predicate.test(value));
    }
}
