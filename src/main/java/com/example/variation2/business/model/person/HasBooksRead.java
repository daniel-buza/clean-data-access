package com.example.variation2.business.model.person;

import com.example.variation2.business.model.book.HasIsbn;

import java.util.List;

public interface HasBooksRead {
    List<HasIsbn> getBooksRead();
}
