package org.hglteam.validation;

import lombok.Getter;
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
                .then(ValidationError.using(RuntimeException::new)
                        .message("wrong! {0}")
                        .andArguments(book.year));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year == 1967)
                .then(ValidationError.using(RuntimeException::new)
                        .message("good!")
                        .andNoArguments());

        assertThrows(RuntimeException.class, () -> validation2.validate(book));
    }

    @Test
    void oneValidationWithConjunctiveConditions() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .and(b -> !b.getName().contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new,"wrong!", book.year));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year == 1967)
                .and(b -> b.getName().contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new,"good!", book.year));

        assertThrows(RuntimeException.class, () -> validation2.validate(book));
    }

    @Test
    void oneValidationWithDisjunctiveConditions() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .or(b -> !b.getName().contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new, "wrong!", book.year));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .or(b -> b.getName().contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new,"good!", book.year));

        assertThrows(RuntimeException.class, () -> validation2.validate(book));
    }

    @Test
    void twoValidationWithSingleConditions() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationError.withMessage(RuntimeException::new,"wrong! %d", book.year))
                .when(b -> !b.getName().contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new,"wrong! %s", book.name));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationError.withMessage(RuntimeException::new,"wrong! %d", book.year))
                .when(b -> b.getName().contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new,"good!", book.name));

        assertThrows(RuntimeException.class, () -> validation2.validate(book), "good!");
    }

    @Test
    void threeValidationOneOverProperty() {
        var validation1 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationError.withMessage(RuntimeException::new, "wrong year!"))
                .onProperty(Book::getAuthor, builder -> builder
                        .when(b -> !b.getName().contains("Garcia"))
                        .then(ValidationError.withMessage(RuntimeException::new, "wrong author name!")))
                .when(b -> !b.name.contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new, "wrong book name!"));

        assertDoesNotThrow(() -> validation1.validate(book));

        var validation2 = Validation.<Book>builder()
                .when(b -> b.year != 1967)
                .then(ValidationError.withMessage(RuntimeException::new, "wrong year!"))
                .onProperty(Book::getAuthor, builder -> builder
                        .when(b -> b.getName().contains("Garcia"))
                        .then(ValidationError.from(RuntimeException::new)))
                .when(b -> b.name.contains("soledad"))
                .then(ValidationError.withMessage(RuntimeException::new, "wrong book name!"));

        assertThrows(RuntimeException.class, () -> validation2.validate(book));
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
