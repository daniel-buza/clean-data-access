package com.example.variation1.persistence;

import lombok.extern.slf4j.Slf4j;
import com.example.variation1.business.adapters.LibraryPersistenceProvider;
import com.example.variation1.business.model.Book;
import com.example.variation1.business.model.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IndependentLibraryPersistenceProviderImpl implements LibraryPersistenceProvider {

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

    private final class LoadingContext {
        final Map<String, Book> booksLoadedAlready = new HashMap<>();
        final Map<String, Person> peopleLoadedAlready = new HashMap<>();
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
        return findPersonByNameInternal(name, new LoadingContext());
    }

    private Person findPersonByNameInternal(final String name, final LoadingContext loadingContext) {
        final var alreadyLoaded = loadingContext.peopleLoadedAlready.get(name);
        if (alreadyLoaded != null) {
            log.info("Not looking up person with name '{}', as already in the loading context", name);
            return alreadyLoaded;
        }
        log.info("Looking up person with name '{}'", name);

        final var personInDB = people.get(name);
        if (personInDB == null) { return null; }

        final var person = new Person()
                .setName((String) personInDB.get(PERSON_ATTRIBUTE_NAME))
                .setAddress((String) personInDB.get(PERSON_ATTRIBUTE_ADDRESS))
                .setPhoneNumber((String) personInDB.get(PERSON_ATTRIBUTE_PHONE));
        loadingContext.peopleLoadedAlready.put(name, person);

        final var booksReadInDB = personInDB.get(PERSON_ATTRIBUTE_BOOKS);
        if (booksReadInDB != null) {
            final List<String> bookIsbnsInDB = (List<String>) booksReadInDB;
            for (final var isbn : bookIsbnsInDB) {
                person.getBooksRead().add(findBookByIsbnInternal(isbn, loadingContext));
            }
        }

        return person;
    }

    @Override
    public Book findBookByIsbn(final String isbn) {
        return findBookByIsbnInternal(isbn, new LoadingContext());
    }

    @Override
    public List<Book> getBooks() {
        return books.keySet().stream().map(this::findBookByIsbn).toList();
    }

    private Book findBookByIsbnInternal(final String isbn, final LoadingContext loadingContext) {
        final var alreadyLoaded = loadingContext.booksLoadedAlready.get(isbn);
        if (alreadyLoaded != null) {
            log.info("Not looking up book with isbn '{}', as already in the loading context", isbn);
            return alreadyLoaded;
        }
        log.info("Looking up book with isbn '{}'", isbn);

        final var bookInDb = books.get(isbn);
        if (bookInDb == null) { return null; }

        final var book = new Book()
                .setIsbn((String) bookInDb.get(BOOK_ATTRIBUTE_ISBN))
                .setTitle((String) bookInDb.get(BOOK_ATTRIBUTE_TITLE))
                .setNumberOfPages((int) bookInDb.get(BOOK_ATTRIBUTE_PAGES));
        loadingContext.booksLoadedAlready.put(isbn, book);

        final var peopleInDB = bookInDb.get(BOOK_ATTRIBUTE_READERS);
        if (peopleInDB != null) {
            final var peopleNamesInDB = (List<String>) peopleInDB;
            for (final var name : peopleNamesInDB) {
                book.getReaders().add(findPersonByNameInternal(name, loadingContext));
            }
        }

        return book;
    }
}
