package com.example;

import lombok.extern.slf4j.Slf4j;
import com.example.variation1.business.model.Book;
import com.example.variation1.business.model.Person;
import com.example.variation1.business.services.LibraryServiceImpl;
import com.example.variation1.persistence.IndependentLibraryPersistenceProviderImpl;

import java.util.List;

@Slf4j
public class LibraryServiceTest_Variation1a extends LibraryServiceTest {
    public LibraryService getCleanLibrary() { return new LibraryServiceImpl(new IndependentLibraryPersistenceProviderImpl()); }

    protected int getNumberOfPages(Object book) { return ((Book)book).getNumberOfPages(); }
    protected String getIsbn(Object book) { return ((Book)book).getIsbn(); }
    protected String getTitle(Object book) { return ((Book)book).getTitle(); }
    protected List<?> getReaders(Object book) { return ((Book)book).getReaders(); }

    protected String getPhoneNumber(Object person) { return ((Person)person).getPhoneNumber(); }
    protected String getName(Object person) { return ((Person)person).getName(); }
    protected String getAddress(Object person) { return ((Person)person).getAddress(); }
    protected List<?> getBooksRead(Object person) { return ((Person)person).getBooksRead(); }
}
