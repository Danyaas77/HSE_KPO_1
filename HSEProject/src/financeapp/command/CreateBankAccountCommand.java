package financeapp.command;

import financeapp.facade.BankAccountFacade;
import financeapp.factory.DomainObjectFactory;
import financeapp.model.BankAccount;

public class CreateBankAccountCommand implements Command {
    private BankAccountFacade bankAccountFacade;
    private String name;
    private double balance;

    public CreateBankAccountCommand(BankAccountFacade facade, String name, double balance) {
        this.bankAccountFacade = facade;
        this.name = name;
        this.balance = balance;
    }

    public void execute() {
        BankAccount account = DomainObjectFactory.createBankAccount(name, balance);
        bankAccountFacade.addAccount(account);
    }
}