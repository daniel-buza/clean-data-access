package com.example;

import java.util.List;

public interface LibraryService {

    void createNewBook(final String isbn,
                       final String title,
                       final int numberOfPages);

    Object findBookByIsbn(String isbn);

    void createNewReader(String name,
                         String address,
                         String phoneNumber);

    Object findReaderByName(String name);

    void borrowBook(String nameOfPerson,
                    String isbnOfBook);

    List<String> listAllBooksWithLengths();
}
