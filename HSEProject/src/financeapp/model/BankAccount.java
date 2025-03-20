package financeapp.model;

import financeapp.importer.Identifiable;
import financeapp.visitor.DataExportVisitor;
import java.util.UUID;

public class BankAccount implements Identifiable {
    private final UUID id;
    private String name;
    private double balance;

    public BankAccount(UUID id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Метод для реализации паттерна Посетитель
    public void accept(DataExportVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "BankAccount{id=" + id + ", name='" + name + "', balance=" + balance + "}";
    }
}