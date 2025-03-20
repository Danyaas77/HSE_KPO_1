package financeapp.visitor;

import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.Operation;

public interface DataExportVisitor {
    void visit(BankAccount account);
    void visit(Category category);
    void visit(Operation operation);
}