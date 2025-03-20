package financeapp.facade;

import financeapp.model.Category;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CategoryFacade {
    private Map<UUID, Category> categories = new HashMap<>();

    public void addCategory(Category category) {
        categories.put(category.getId(), category);
        System.out.println("Category added: " + category);
    }

    public Category getCategory(UUID id) {
        return categories.get(id);
    }

    public Collection<Category> getAllCategories() {
        return categories.values();
    }

    public void updateCategory(Category category) {
        categories.put(category.getId(), category);
        System.out.println("Category updated: " + category);
    }

    public void removeCategory(UUID id) {
        categories.remove(id);
        System.out.println("Category removed with id: " + id);
    }
}