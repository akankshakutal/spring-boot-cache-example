package com.example.caching;

import org.springframework.cache.Cache;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

class ConcurrentMapCollectionHandlingDecoratedCache extends CollectionHandlingDecoratedCache {

    protected ConcurrentMapCollectionHandlingDecoratedCache(final Cache cache) {
        super(cache);
    }

    @Override
    @SuppressWarnings("all")
    protected boolean areAllKeysPresentInCache(Iterable<?> keys) {

        ConcurrentMap nativeCache = (ConcurrentMap) getNativeCache();

        return StreamSupport.stream(keys.spliterator(), false).allMatch(nativeCache::containsKey);
    }
}

