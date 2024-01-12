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
public class Person {
    private String name;
    private String address;
    private String phoneNumber;
    private List<Book> booksRead = new ArrayList<>();
}
