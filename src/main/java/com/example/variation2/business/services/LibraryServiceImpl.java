package com.example.variation2.business.services;

import lombok.RequiredArgsConstructor;
import com.example.LibraryService;
import com.example.variation2.business.adapters.LibraryPersistenceProvider;
import com.example.variation2.business.model.book.Book;
import com.example.variation2.business.model.book.HasIsbn;
import com.example.variation2.business.model.person.HasName;
import com.example.variation2.business.model.person.Person;

import java.util.List;

@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final LibraryPersistenceProvider persistenceProvider;

    @Override
    public void createNewBook(String isbn, String title, int numberOfPages) {
        persistenceProvider.saveBook(new Book() {
            public String getIsbn() { return isbn; }
            public int getNumberOfPages() { return numberOfPages; }
            public List<HasName> getReaders() { return List.of(); }
            public String getTitle() { return title; }
        });
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        return persistenceProvider.findBookByIsbn(isbn);
    }

    @Override
    public void createNewReader(String name, String address, String phoneNumber) {
        persistenceProvider.savePerson(new Person() {
            public String getAddress() { return address; }
            public List<HasIsbn> getBooksRead() { return List.of(); }
            public String getName() { return name; }
            public String getPhoneNumber() { return phoneNumber; }
        });
    }

    @Override
    public Person findReaderByName(String name) {
        return persistenceProvider.findPersonByName(name);
    }

    @Override
    public void borrowBook(String nameOfPerson, String isbnOfBook) {
        final Person person = persistenceProvider.findPersonByName(nameOfPerson);
        final Book book = persistenceProvider.findBookByIsbn(isbnOfBook);

        person.getBooksRead().add(book);
        book.getReaders().add(person);

        persistenceProvider.savePerson(person);
        persistenceProvider.saveBook(book);
    }

    @Override
    public List<String> listAllBooksWithLengths() {
        return persistenceProvider.getBooks().stream()
                .map(book -> "%s with %d page(s)".formatted(book.getTitle(), book.getNumberOfPages()))
                .toList();
    }
}
