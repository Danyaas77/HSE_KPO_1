package financeapp.model;

import financeapp.importer.Identifiable;
import financeapp.visitor.DataExportVisitor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Operation implements Identifiable {
    private final UUID id;
    private final OperationType type;
    private final UUID bankAccountId;
    private final double amount;
    private final Date date;
    private final String description;
    private final UUID categoryId;

    public Operation(UUID id, OperationType type, UUID bankAccountId, double amount, Date date, String description, UUID categoryId) {
        this.id = id;
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.categoryId = categoryId;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public OperationType getType() {
        return type;
    }

    public UUID getBankAccountId() {
        return bankAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void accept(DataExportVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Operation{id=" + id + ", type=" + type + ", bankAccountId=" + bankAccountId +
                ", amount=" + amount + ", date=" + sdf.format(date) + ", description='" + description +
                "', categoryId=" + categoryId + "}";
    }
}