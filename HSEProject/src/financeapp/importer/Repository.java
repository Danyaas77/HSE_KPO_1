package financeapp.importer;

import java.util.Collection;
import java.util.UUID;

public interface Repository<T> {
    void add(T item);
    T getById(UUID id);
    Collection<T> getAll();
    void update(T item);
    void remove(UUID id);
}
