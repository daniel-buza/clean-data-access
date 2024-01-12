package com.example.variation2.business.adapters;


import com.example.variation2.business.model.book.Book;
import com.example.variation2.business.model.book.HasNumberOfPages;
import com.example.variation2.business.model.book.HasTitle;
import com.example.variation2.business.model.person.Person;

import java.util.List;

public interface LibraryPersistenceProvider {
    void saveBook(Book book);
    void savePerson(Person person);
    Person findPersonByName(String name);
    Book findBookByIsbn(String isbn);
    <T extends HasTitle & HasNumberOfPages> List<T> getBooks();
}
