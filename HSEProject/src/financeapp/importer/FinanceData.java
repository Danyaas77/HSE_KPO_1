package financeapp.importer;

import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.Operation;

import java.util.List;

public class FinanceData {
    private List<BankAccount> bankAccounts;
    private List<Category> categories;
    private List<Operation> operations;

    public FinanceData() {
    }

    public FinanceData(List<BankAccount> bankAccounts, List<Category> categories, List<Operation> operations) {
        this.bankAccounts = bankAccounts;
        this.categories = categories;
        this.operations = operations;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}

