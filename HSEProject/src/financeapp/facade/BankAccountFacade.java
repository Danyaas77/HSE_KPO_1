package financeapp.facade;

import financeapp.model.BankAccount;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankAccountFacade {
    private Map<UUID, BankAccount> accounts = new HashMap<>();

    public void addAccount(BankAccount account) {
        accounts.put(account.getId(), account);
        System.out.println("BankAccount added: " + account);
    }

    public BankAccount getAccount(UUID id) {
        return accounts.get(id);
    }

    public Collection<BankAccount> getAllAccounts() {
        return accounts.values();
    }

    public void updateAccount(BankAccount account) {
        accounts.put(account.getId(), account);
        System.out.println("BankAccount updated: " + account);
    }

    public void removeAccount(UUID id) {
        accounts.remove(id);
        System.out.println("BankAccount removed with id: " + id);
    }
}