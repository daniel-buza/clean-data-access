package com.example;

import lombok.extern.slf4j.Slf4j;
import com.example.variation2.business.model.book.HasIsbn;
import com.example.variation2.business.model.book.HasNumberOfPages;
import com.example.variation2.business.model.book.HasReaders;
import com.example.variation2.business.model.book.HasTitle;
import com.example.variation2.business.model.person.HasAddress;
import com.example.variation2.business.model.person.HasBooksRead;
import com.example.variation2.business.model.person.HasName;
import com.example.variation2.business.model.person.HasPhoneNumber;
import com.example.variation2.business.services.LibraryServiceImpl;
import com.example.variation2.persistence.LibraryPersistenceProviderImpl;
import java.util.List;

@Slf4j
public class LibraryServiceTest_Variation2 extends LibraryServiceTest {
    public LibraryService getCleanLibrary() { return new LibraryServiceImpl(new LibraryPersistenceProviderImpl()); }

    protected int getNumberOfPages(Object book) { return ((HasNumberOfPages)book).getNumberOfPages(); }
    protected String getIsbn(Object book) { return ((HasIsbn)book).getIsbn(); }
    protected String getTitle(Object book) { return ((HasTitle)book).getTitle(); }
    protected List<?> getReaders(Object book) { return ((HasReaders)book).getReaders(); }

    protected String getPhoneNumber(Object person) { return ((HasPhoneNumber)person).getPhoneNumber(); }
    protected String getName(Object person) { return ((HasName)person).getName(); }
    protected String getAddress(Object person) { return ((HasAddress)person).getAddress(); }
    protected List<?> getBooksRead(Object person) { return ((HasBooksRead)person).getBooksRead(); }
}
