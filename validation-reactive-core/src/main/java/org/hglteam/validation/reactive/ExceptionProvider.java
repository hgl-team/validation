package org.hglteam.validation.reactive;

public interface ExceptionProvider<T, E extends RuntimeException> {
    E create(T source);
}
