package financeapp.importer;

import java.util.UUID;

/**
 * Интерфейс, который должны реализовывать все доменные объекты,
 * чтобы их можно было идентифицировать по уникальному идентификатору.
 */
public interface Identifiable {
    UUID getId();
}

