package com.example.variation1.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.variation1.business.adapters.LibraryPersistenceProvider;
import com.example.variation1.business.model.Book;
import com.example.variation1.business.model.Person;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
public class RelatedLibraryPersistenceProviderImpl implements LibraryPersistenceProvider {

    final Map<String, Map<String, Object>> books = new HashMap<>();
    private final String BOOK_ATTRIBUTE_TITLE = "title";
    private final String BOOK_ATTRIBUTE_ISBN = "isbn";
    private final String BOOK_ATTRIBUTE_PAGES = "pages";
    private final String BOOK_ATTRIBUTE_READERS = "readers";

    final Map<String, Map<String, Object>> people = new HashMap<>();
    private final String PERSON_ATTRIBUTE_NAME = "name";
    private final String PERSON_ATTRIBUTE_ADDRESS = "addr";
    private final String PERSON_ATTRIBUTE_PHONE = "phone";
    private final String PERSON_ATTRIBUTE_BOOKS = "books";


    @RequiredArgsConstructor
    public class BookFromDB extends Book {
        private final Supplier<List<Person>> readersLoader;
        private boolean shallUseProxy = true;

        @Override
        public List<Person> getReaders() {
            if (shallUseProxy) {
                super.setReaders(readersLoader.get());
                shallUseProxy = false;
            }
            return super.getReaders();
        }

        @Override
        public Book setReaders(final List<Person> readers) {
            shallUseProxy = false;
            return super.setReaders(readers);
        }
    }

    @RequiredArgsConstructor
    public class PersonFromDB extends Person {
        private final Supplier<List<Book>> booksReader;
        private boolean shallUseProxy = true;

        @Override
        public List<Book> getBooksRead() {
            if (shallUseProxy) {
                super.setBooksRead(booksReader.get());
                shallUseProxy = false;
            }
            return super.getBooksRead();
        }

        @Override
        public Person setBooksRead(final List<Book> books) {
            shallUseProxy = false;
            return super.setBooksRead(books);
        }
    }

    @Override
    public void saveBook(Book book) {
        log.info("Saving a book with ISBN {}", book.getIsbn());
        books.putIfAbsent(book.getIsbn(), new HashMap<>());
        final var bookInDB = books.get(book.getIsbn());

        bookInDB.put(BOOK_ATTRIBUTE_PAGES, book.getNumberOfPages());
        bookInDB.put(BOOK_ATTRIBUTE_ISBN, book.getIsbn());
        bookInDB.put(BOOK_ATTRIBUTE_TITLE, book.getTitle());
        bookInDB.put(BOOK_ATTRIBUTE_READERS, book.getReaders().stream().map(Person::getName).toList());
    }

    @Override
    public void savePerson(final Person person) {
        log.info("Saving a person with name {}", person.getName());
        people.putIfAbsent(person.getName(), new HashMap<>());
        final var personInDB = people.get(person.getName());

        personInDB.put(PERSON_ATTRIBUTE_PHONE, person.getPhoneNumber());
        personInDB.put(PERSON_ATTRIBUTE_ADDRESS, person.getAddress());
        personInDB.put(PERSON_ATTRIBUTE_NAME, person.getName());
        personInDB.put(PERSON_ATTRIBUTE_BOOKS, person.getBooksRead().stream().map(Book::getIsbn).toList());
    }

    @Override
    public Person findPersonByName(final String name) {
        log.info("Looking up person with name '{}'", name);

        final var personInDB = people.get(name);
        if (personInDB == null) { return null; }

        final var booksReadInDB = personInDB.get(PERSON_ATTRIBUTE_BOOKS);
        final Supplier<List<Book>> booksLoader;
        if (booksReadInDB != null) {
            final List<String> bookIsbnsInDB = (List<String>) booksReadInDB;
            booksLoader = () -> new ArrayList<>(bookIsbnsInDB.stream().map(this::findBookByIsbn).toList());
        } else {
            booksLoader = () -> null;
        }

        final var person = new PersonFromDB(booksLoader)
                .setName((String) personInDB.get(PERSON_ATTRIBUTE_NAME))
                .setAddress((String) personInDB.get(PERSON_ATTRIBUTE_ADDRESS))
                .setPhoneNumber((String) personInDB.get(PERSON_ATTRIBUTE_PHONE));

        return person;
    }

    @Override
    public Book findBookByIsbn(final String isbn) {
        log.info("Looking up book with isbn '{}'", isbn);

        final var bookInDb = books.get(isbn);
        if (bookInDb == null) { return null; }

        final var peopleInDB = bookInDb.get(BOOK_ATTRIBUTE_READERS);
        final Supplier<List<Person>> readersLoader;
        if (peopleInDB != null) {
            final var peopleNamesInDB = (List<String>) peopleInDB;
            readersLoader = () -> new ArrayList(peopleNamesInDB.stream().map(this::findPersonByName).toList());
        } else {
            readersLoader = () -> null;
        }

        final var book = new BookFromDB(readersLoader)
                .setIsbn((String) bookInDb.get(BOOK_ATTRIBUTE_ISBN))
                .setTitle((String) bookInDb.get(BOOK_ATTRIBUTE_TITLE))
                .setNumberOfPages((int) bookInDb.get(BOOK_ATTRIBUTE_PAGES));
        return book;
    }

    @Override
    public List<Book> getBooks() {
        return books.keySet().stream().map(this::findBookByIsbn).toList();
    }

}
