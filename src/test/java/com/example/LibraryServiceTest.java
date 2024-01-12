package com.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public abstract class LibraryServiceTest {

    private LibraryService service;

    @BeforeEach
    public void reset() { service = getCleanLibrary(); }
    protected abstract LibraryService getCleanLibrary();

    protected abstract int getNumberOfPages(Object book);
    protected abstract String getIsbn(Object book);
    protected abstract String getTitle(Object book);
    protected abstract List<?> getReaders(Object book);

    protected abstract String getPhoneNumber(Object person);
    protected abstract String getName(Object person);
    protected abstract String getAddress(Object person);
    protected abstract List<?> getBooksRead(Object person);

    @Test
    public void testBookCreation() {
        assertNull(service.findBookByIsbn("isbn111"), "Book shall not be existing before creating it");

        service.createNewBook("isbn111", "title11", 34);

        final var loadedBook = service.findBookByIsbn("isbn111");
        assertEquals(34, getNumberOfPages(loadedBook));
        assertEquals("isbn111", getIsbn(loadedBook));
        assertEquals("title11", getTitle(loadedBook));
    }

    @Test
    public void testPersonCreation() {
        assertNull(service.findReaderByName("name111"), "Person shall not be existing before creating it");

        service.createNewReader("name111", "ad1", "213");

        final var loadedPerson = service.findReaderByName("name111");
        assertEquals("213", getPhoneNumber(loadedPerson));
        assertEquals("name111", getName(loadedPerson));
        assertEquals("ad1", getAddress(loadedPerson));
    }

    @Test
    public void testChainFromPerson1() {
        saveChainOfEntities();
        final var person1 = service.findReaderByName("name1");
        asserChainOfEntities(person1);
    }

    @Test
    public void testChainFromBook2() {
        saveChainOfEntities();
        final var book2 = service.findBookByIsbn("isbn2");
        asserChainOfEntities(getReaders(book2).get(0));
    }

    @Test
    public void testChainFromPerson3() {
        saveChainOfEntities();
        final var person3 = service.findReaderByName("name3");
        asserChainOfEntities(getReaders(getBooksRead(person3).get(0)).get(0));
    }

    @Test
    public void testChainFromBook4() {
        saveChainOfEntities();
        final var book4 = service.findBookByIsbn("isbn4");
        asserChainOfEntities(getReaders(getBooksRead(getReaders(book4).get(0)).get(0)).get(0));
    }

    @Test
    public void testListAllBooks() {
        service.createNewBook("isbn111", "title1", 134);
        service.createNewBook("isbn234", "title2", 356);

        final var allBooks = service.listAllBooksWithLengths();

        assertTrue(allBooks.contains("title1 with 134 page(s)"));
        assertTrue(allBooks.contains("title2 with 356 page(s)"));
    }

    private void saveChainOfEntities() {
        log.info("Setting up chain of entities");
        service.createNewReader("name1", null, null);
        service.createNewBook("isbn2", null, 0);
        service.createNewReader("name3", null, null);
        service.createNewBook("isbn4", null, 0);

        service.borrowBook("name1", "isbn2");
        service.borrowBook("name3", "isbn2");
        service.borrowBook("name3", "isbn4");
        log.info("Chain of entities is set up");
    }

    private void asserChainOfEntities(final Object person1) {
        log.info("Asserting chain of entities");
        final var book2 = getBooksRead(person1).get(0);
        final var person3 = getReaders(book2).get(1);
        final var book4 = getBooksRead(person3).get(1);

        assertEquals(1, getBooksRead(person1).size());
        assertEquals(2, getReaders(book2).size());
        assertEquals(2, getBooksRead(person3).size());
        assertEquals(1, getReaders(book4).size());

        assertEquals("name1", getName(person1));
        assertEquals("isbn2", getIsbn(book2));
        assertEquals("name3", getName(person3));
        assertEquals("isbn4", getIsbn(book4));

        assertEquals(getTitle(book2), getTitle(getBooksRead(person1).get(0)));
        assertEquals(getName(person1), getName(getReaders(book2).get(0)));
        assertEquals(getName(person3), getName(getReaders(book2).get(1)));
        assertEquals(getTitle(book2), getTitle(getBooksRead(person3).get(0)));
        assertEquals(getTitle(book4), getTitle(getBooksRead(person3).get(1)));
        assertEquals(getName(person3), getName(getReaders(book4).get(0)));
        log.info("Chain of entities asserted");
    }
}
