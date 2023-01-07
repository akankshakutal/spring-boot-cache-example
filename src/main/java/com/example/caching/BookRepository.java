package com.example.caching;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookRepository {
    public Book getByIsbn(String isbn) {
        return new Book(isbn, "Some book");
    }

    public List<Book> getAllBooks(List<String> values) {
        List<Book> results = new ArrayList<>(values.size());
        results.addAll(values.stream().map(this::getByIsbn).collect(Collectors.toList()));
        return results;
    }

}
