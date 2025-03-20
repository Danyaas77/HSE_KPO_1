package financeapp.model;

import financeapp.importer.Identifiable;
import financeapp.visitor.DataExportVisitor;
import java.util.UUID;

public class Category implements Identifiable {
    private final UUID id;
    private final CategoryType type;
    private String name;

    public Category(UUID id, CategoryType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public CategoryType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void accept(DataExportVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Category{id=" + id + ", type=" + type + ", name='" + name + "'}";
    }
}