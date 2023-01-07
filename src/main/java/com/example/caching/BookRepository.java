package com.example.caching;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookRepository {

    private boolean cacheMiss;

    public synchronized void setCacheMiss(boolean cacheMiss) {
        this.cacheMiss = cacheMiss;
    }


    public synchronized boolean isCacheMiss() {
        boolean cacheMiss = this.cacheMiss;
        setCacheMiss(false);
        return cacheMiss;
    }


    @Cacheable(value = "books")
    public Book getByIsbn(String isbn) {
        setCacheMiss(true);
        return new Book(isbn, "Some book");
    }


    @Cacheable(value = "books")
    public List<Book> getAllBooks(List<String> values) {
        setCacheMiss(true);
        List<Book> results = new ArrayList<>(values.size());
        results.addAll(values.stream().map(this::getByIsbn).collect(Collectors.toList()));
        return results;
    }

}
