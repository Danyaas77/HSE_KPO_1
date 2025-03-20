package financeapp.visitor;

import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.Operation;

import java.text.SimpleDateFormat;

public class ExportToCSVVisitor implements DataExportVisitor {
    private StringBuilder sb = new StringBuilder();
    // Можно задать формат для даты
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void visit(BankAccount account) {
        sb.append("BankAccount,")
                .append(account.getId()).append(",")
                .append(account.getName()).append(",")
                .append(account.getBalance()).append("\n");
    }

    @Override
    public void visit(Category category) {
        sb.append("Category,")
                .append(category.getId()).append(",")
                .append(category.getType()).append(",")
                .append(category.getName()).append("\n");
    }

    @Override
    public void visit(Operation operation) {
        sb.append("Operation,")
                .append(operation.getId()).append(",")
                .append(operation.getType()).append(",")
                .append(operation.getBankAccountId()).append(",")
                .append(operation.getAmount()).append(",")
                .append(sdf.format(operation.getDate())).append(",")
                .append(operation.getDescription() != null ? operation.getDescription() : "").append(",")
                .append(operation.getCategoryId()).append("\n");
    }

    public String getResult() {
        return sb.toString();
    }
}
