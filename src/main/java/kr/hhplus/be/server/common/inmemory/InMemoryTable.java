package kr.hhplus.be.server.common.inmemory;

import java.util.List;
import java.util.Map;

public interface InMemoryTable<K, V> {
    V select(K key);
    void insert(K key, V value);
    void delete(K key);
    void clear();

    List<V> selectAll();
}
