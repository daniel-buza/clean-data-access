package com.example.variation1.business.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Book {
    private String title;
    private String isbn;
    private int numberOfPages;
    private List<Person> readers = new ArrayList<>();
}
