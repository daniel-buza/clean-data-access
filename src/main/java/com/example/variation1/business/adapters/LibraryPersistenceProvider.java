package com.example.variation1.business.adapters;

import com.example.variation1.business.model.Book;
import com.example.variation1.business.model.Person;

import java.util.List;

public interface LibraryPersistenceProvider {
    void saveBook(Book book);
    void savePerson(Person person);
    Person findPersonByName(String name);
    Book findBookByIsbn(String isbn);
    List<Book> getBooks();
}
