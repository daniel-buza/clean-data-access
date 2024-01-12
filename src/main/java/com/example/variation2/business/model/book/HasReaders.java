package com.example.variation2.business.model.book;

import com.example.variation2.business.model.person.HasName;

import java.util.List;

public interface HasReaders {
    List<HasName> getReaders();
}
