package com.example.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final BookRepository bookRepository;

    @Autowired
    private CachingService cachingService;

    @Autowired
    private ApplicationContext context;

    public AppRunner(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        var values = List.of("isbn-1234", "isbn-4567");
        List<Book> allBooks = bookRepository.getAllBooks(values);

        cachingService.setBooks(allBooks);

        logger.info(".... Fetching books");

        logger.info("All books -->" + cachingService.cacheBooksData(values));
        logger.info("isCacheMiss -->" + cachingService.isCacheMiss());

        logger.info("isbn-1234 -->" + cachingService.CacheBookData("isbn-1234"));
        logger.info("isbn-1234 -->" + cachingService.CacheBookData("isbn-4567"));
        logger.info("isCacheMiss -->" + cachingService.isCacheMiss());

        logger.info("isbn-1234 -->" + cachingService.CacheBookData("isbn-2345"));
        logger.info("isCacheMiss -->" + cachingService.isCacheMiss());

        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        Object books = cacheManager.getCache("books").getNativeCache();

        logger.info("****************************" + books);
    }

}
