package financeapp.facade;

import financeapp.model.BankAccount;
import financeapp.model.Operation;
import financeapp.model.OperationType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OperationFacade {
    // Изменено на Map с ключом типа UUID
    private Map<UUID, Operation> operations = new HashMap<>();
    private BankAccountFacade bankAccountFacade;
    private CategoryFacade categoryFacade;

    public OperationFacade(BankAccountFacade bankAccountFacade, CategoryFacade categoryFacade) {
        this.bankAccountFacade = bankAccountFacade;
        this.categoryFacade = categoryFacade;
    }

    public void addOperation(Operation operation) {
        operations.put(operation.getId(), operation);
        // Обновляем баланс счета
        BankAccount account = bankAccountFacade.getAccount(operation.getBankAccountId());
        if (account != null) {
            if (operation.getType() == OperationType.INCOME) {
                account.setBalance(account.getBalance() + operation.getAmount());
            } else {
                account.setBalance(account.getBalance() - operation.getAmount());
            }
            bankAccountFacade.updateAccount(account);
        }
        System.out.println("Operation added: " + operation);
    }

    public Operation getOperation(UUID id) {
        return operations.get(id);
    }

    public Collection<Operation> getAllOperations() {
        return operations.values();
    }

    public void updateOperation(Operation operation) {
        operations.put(operation.getId(), operation);
        System.out.println("Operation updated: " + operation);
    }

    public void removeOperation(UUID id) {
        operations.remove(id);
        System.out.println("Operation removed with id: " + id);
    }
}