package financeapp.importer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Простейшая in‑memory реализация репозитория с использованием HashMap.
 */
public class InMemoryRepository<T extends Identifiable> implements Repository<T> {
    private Map<UUID, T> storage = new HashMap<>();

    @Override
    public void add(T item) {
        storage.put(item.getId(), item);
    }

    @Override
    public T getById(UUID id) {
        return storage.get(id);
    }

    @Override
    public Collection<T> getAll() {
        return storage.values();
    }

    @Override
    public void update(T item) {
        if (storage.containsKey(item.getId())) {
            storage.put(item.getId(), item);
        }
    }

    @Override
    public void remove(UUID id) {
        storage.remove(id);
    }
}

