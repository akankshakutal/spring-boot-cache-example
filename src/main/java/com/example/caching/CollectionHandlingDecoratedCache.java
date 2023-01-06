package com.example.caching;

import javafx.util.Pair;
import org.springframework.cache.Cache;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

abstract class CollectionHandlingDecoratedCache implements Cache {

    private final Cache cache;

    protected CollectionHandlingDecoratedCache(Cache cache) {
        this.cache = cache;
    }

    protected Cache getCache() {
        return this.cache;
    }

    @Override
    public String getName() {
        return getCache().getName();
    }

    @Override
    public Object getNativeCache() {
        return getCache().getNativeCache();
    }

    protected abstract boolean areAllKeysPresentInCache(Iterable<?> keys);

    @SuppressWarnings("unused")
    protected int sizeOf(Iterable<?> iterable) {
        return Long.valueOf(StreamSupport.stream(iterable.spliterator(), false).count()).intValue();
    }

    protected <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("all")
    public ValueWrapper get(Object key) {

        if (key instanceof Iterable) {

            Iterable<?> keys = (Iterable<?>) key;

            if (!areAllKeysPresentInCache(keys)) {
                return null;
            }

            Collection<Object> values = new ArrayList<>();

            for (Object singleKey : keys) {
                values.add(getCache().get(singleKey).get());
            }

            return () -> values;
        }

        return getCache().get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {

        if (key instanceof Iterable) {

            Assert.isAssignable(Iterable.class, type,
                String.format("Expected return type [%1$s] must be Iterable when querying multiple keys [%2$s]",
                    type.getName(), key));

            return (T) Optional.ofNullable(get(key)).map(ValueWrapper::get).orElse(null);
        }

        return getCache().get(key, type);
    }

    @Override
    @SuppressWarnings("all")
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) get(key, Object.class);
    }

    @Override
    public void put(@NonNull Object key, Object value) {

        if (key instanceof Iterable) {
            pairsFromKeysAndValues(toList((Iterable<?>) key), toList((Iterable<?>) value))
                .forEach(pair -> getCache().put(pair.getKey(), pair.getValue()));
        } else {
            getCache().put(key, value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {

        if (key instanceof Iterable) {
            return () -> pairsFromKeysAndValues(toList((Iterable<?>) key), toList((Iterable<?>) value)).stream()
                .map(pair -> getCache().putIfAbsent(pair.getKey(), pair.getValue()))
                .collect(Collectors.toList());
        }

        return getCache().putIfAbsent(key, value);
    }

    @Override
    @SuppressWarnings("all")
    public void evict(Object key) {

        if (key instanceof Iterable) {
            StreamSupport.stream(((Iterable) key).spliterator(), false).forEach(getCache()::evict);
        } else {
            getCache().evict(key);
        }
    }

    @Override
    public void clear() {
        getCache().clear();
    }

    private <K, V> List<Pair<K, V>> pairsFromKeysAndValues(List<K> keys, List<V> values) {
        final int keysSize = keys.size();
        return IntStream.range(0, keysSize)
            .mapToObj(index -> new Pair<>(keys.get(index), values.get(index)))
            .collect(Collectors.toList());

    }
}
