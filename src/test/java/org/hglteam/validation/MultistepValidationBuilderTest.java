package org.hglteam.validation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultistepValidationBuilderTest {

    private Book book;

    @BeforeEach
    void setup() {
        this.book = Book.builder()
                .name("Cien anios de soledad")
                .year(1967)
                .author(Author.builder()
                        .name("Gabriel Garcia Marquez")
                        .nationality("Colombia")
                        .build())
                .build();
    }

    @Test
    void oneValidationWithSimpleCondition() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationException.withMessage("wrong!", book.year));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year == 1967)
                .then(ValidationException.withMessage("good!", book.year));

        assertThrows(ValidationException.class, () -> validation2.validate(book));
    }

    @Test
    void oneValidationWithConjunctiveConditions() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .and(b -> !b.getName().contains("soledad"))
                .then(ValidationException.withMessage("wrong!", book.year));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year == 1967)
                .and(b -> b.getName().contains("soledad"))
                .then(ValidationException.withMessage("good!", book.year));

        assertThrows(ValidationException.class, () -> validation2.validate(book));
    }

    @Test
    void oneValidationWithDisjunctiveConditions() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .or(b -> !b.getName().contains("soledad"))
                .then(ValidationException.withMessage("wrong!", book.year));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .or(b -> b.getName().contains("soledad"))
                .then(ValidationException.withMessage("good!", book.year));

        assertThrows(ValidationException.class, () -> validation2.validate(book));
    }

    @Test
    void twoValidationWithSingleConditions() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationException.withMessage("wrong! %d", book.year))
                .when(b -> !b.getName().contains("soledad"))
                .then(ValidationException.withMessage("wrong! %s", book.name));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationException.withMessage("wrong! %d", book.year))
                .when(b -> b.getName().contains("soledad"))
                .then(ValidationException.withMessage("good!", book.name));

        assertThrows(ValidationException.class, () -> validation2.validate(book), "good!");
    }

    @Test
    void threeValidationOneOverProperty() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationException.withMessage("wrong year!"))
                .onProperty(Book::getAuthor, builder -> builder
                        .when(b -> !b.getName().contains("Garcia"))
                        .then(ValidationException.withMessage("wrong author name!")))
                .when(b -> !b.name.contains("soledad"))
                .then(ValidationException.withMessage("wrong book name!"));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationException.withMessage("wrong year!"))
                .onProperty(Book::getAuthor, builder -> builder
                        .when(b -> b.getName().contains("Garcia"))
                        .then(ValidationException.withMessage("good!")))
                .when(b -> b.name.contains("soledad"))
                .then(ValidationException.withMessage("wrong book name!"));

        assertThrows(ValidationException.class, () -> validation2.validate(book), "good!");
    }

    @Getter
    @SuperBuilder(toBuilder = true)
    public static class Author {
        private String name;
        private String nationality;
    }

    @Getter
    @SuperBuilder(toBuilder = true)
    public static class Book {
        private String name;
        private int year;
        private Author author;
    }
}
