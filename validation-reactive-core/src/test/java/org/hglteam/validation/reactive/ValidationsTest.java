package org.hglteam.validation.reactive;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.function.Predicate;

class ValidationsTest {
    public static final String EXPECTED_MESSAGE = "Expected Message";

    @Test
    void when_predicateFails_then_fails() {
        StepVerifier.create(Validations.<Book>builder()
                        .whenValue(book -> Objects.isNull(book.getAuthor()))
                        .then(ValidationError.withMessage(RuntimeException::new, EXPECTED_MESSAGE))
                        .validate(Book.builder().build()))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(EXPECTED_MESSAGE))
                .verify();
    }

    @Test
    void when_predicateOnPropertyFails_then_fails() {
        StepVerifier.create(Validations.<Book>builder()
                        .onProperty(Book::getAuthor, author -> author
                                .whenValue(String::isEmpty)
                                .then(ValidationError.withMessage(RuntimeException::new, EXPECTED_MESSAGE)))
                        .validate(Book.builder()
                                .author("")
                                .build()))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(EXPECTED_MESSAGE))
                .verify();
    }

    @Test
    void when_andPredicatesOnPropertyFails_then_fails() {
        StepVerifier.create(Validations.<Book>builder()
                        .onProperty(Book::getAuthor, author -> author
                                .whenValue(String::isBlank)
                                .andValue(Predicate.not(String::isEmpty))
                                .then(ValidationError.withMessage(RuntimeException::new, EXPECTED_MESSAGE)))
                        .validate(Book.builder()
                                .author(" \t")
                                .build()))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(EXPECTED_MESSAGE))
                .verify();
    }

    @Test
    void when_orPredicatesOnPropertyFails_then_fails() {
        StepVerifier.create(Validations.<Book>builder()
                        .onProperty(Book::getAuthor, author -> author
                                .whenValue(String::isEmpty)
                                .orValue(String::isBlank)
                                .then(ValidationError.withMessage(RuntimeException::new, EXPECTED_MESSAGE)))
                        .validate(Book.builder()
                                .author(" \t")
                                .build()))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(EXPECTED_MESSAGE))
                .verify();
    }
}