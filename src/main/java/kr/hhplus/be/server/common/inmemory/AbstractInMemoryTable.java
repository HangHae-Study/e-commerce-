package kr.hhplus.be.server.common.inmemory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractInMemoryTable<K, V> implements InMemoryTable<K, V> {
    protected final Map<K, V> store = new HashMap<>();
    @Override
    public V select(K key) {
        return store.get(key);
    }

    @Override
    public void insert(K key, V value) {
        store.put(key, value);
    }

    @Override
    public void delete(K key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public List<V> selectAll(){return (List<V>) store.values();};


}

