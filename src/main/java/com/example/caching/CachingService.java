package com.example.caching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CachingService {
    private boolean cacheMiss;

    @Autowired
    private BookRepository bookRepository;

    private List<Book> books;

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public synchronized void setCacheMiss(boolean cacheMiss) {
        this.cacheMiss = cacheMiss;
    }


    public synchronized boolean isCacheMiss() {
        boolean cacheMiss = this.cacheMiss;
        setCacheMiss(false);
        return cacheMiss;
    }

    @Cacheable(value = "books")
    public List<Book> cacheBooksData(List<String> values) {
        setCacheMiss(true);
        List<Book> result = values.stream()
            .map(ele -> this.books.stream().filter(book -> book.getIsbn().equals(ele)).toList().get(0))
            .toList();
        setBooks(List.of());
        return result;
    }

    @Cacheable(value = "books")
    public Book CacheBookData(String isbn) {
        setCacheMiss(true);
        return bookRepository.getByIsbn(isbn);
    }

}
