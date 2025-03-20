package financeapp.factory;

import financeapp.model.*;
import java.util.Date;
import java.util.UUID;

public class DomainObjectFactory {

    public static BankAccount createBankAccount(String name, double balance) {
        return new BankAccount(UUID.randomUUID(), name, balance);
    }

    public static Category createCategory(CategoryType type, String name) {
        return new Category(UUID.randomUUID(), type, name);
    }

    public static Operation createOperation(OperationType type, BankAccount account, double amount, Date date, String description, Category category) {
        if (amount < 0) {
            throw new IllegalArgumentException("Operation amount cannot be negative");
        }
        return new Operation(UUID.randomUUID(), type, account.getId(), amount, date, description, category.getId());
    }
}