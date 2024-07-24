package org.hglteam.validation.reactive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Book {
    private String title;
    private String author;
    private String publisher;
    private Integer year;
}
