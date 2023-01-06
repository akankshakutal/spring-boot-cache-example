package com.example.caching;

import java.util.List;

public interface BookRepository {

    Book getByIsbn(String isbn);

    List<Book> getAllBooks(List<String> values);

    boolean isCacheMiss();
}
