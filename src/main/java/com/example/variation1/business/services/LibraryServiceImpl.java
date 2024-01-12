package com.example.variation1.business.services;

import lombok.RequiredArgsConstructor;
import com.example.LibraryService;
import com.example.variation1.business.adapters.LibraryPersistenceProvider;
import com.example.variation1.business.model.Book;
import com.example.variation1.business.model.Person;

import java.util.List;

@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final LibraryPersistenceProvider libraryPersistenceProvider;

    @Override
    public void createNewBook(final String isbn,
                              final String title,
                              final int numberOfPages) {
        final var newBook = new Book()
                .setIsbn(isbn)
                .setTitle(title)
                .setNumberOfPages(numberOfPages);

        libraryPersistenceProvider.saveBook(newBook);
    }

    @Override
    public Book findBookByIsbn(final String isbn) {
        return libraryPersistenceProvider.findBookByIsbn(isbn);
    }

    @Override
    public void createNewReader(final String name,
                                final String address,
                                final String phoneNumber) {
        final var newPerson = new Person()
                .setName(name)
                .setAddress(address)
                .setPhoneNumber(phoneNumber);

        libraryPersistenceProvider.savePerson(newPerson);
    }

    @Override
    public Person findReaderByName(final String name) {
        return libraryPersistenceProvider.findPersonByName(name);
    }

    @Override
    public void borrowBook(final String nameOfPerson,
                           final String isbnOfBook) {
        final Person person = libraryPersistenceProvider.findPersonByName(nameOfPerson);
        final Book book = libraryPersistenceProvider.findBookByIsbn(isbnOfBook);

        person.getBooksRead().add(book);
        book.getReaders().add(person);

        libraryPersistenceProvider.savePerson(person);
        libraryPersistenceProvider.saveBook(book);
    }

    @Override
    public List<String> listAllBooksWithLengths() {
        return libraryPersistenceProvider.getBooks().stream()
                .map(book -> "%s with %d page(s)".formatted(book.getTitle(), book.getNumberOfPages()))
                .toList();
    }

}
