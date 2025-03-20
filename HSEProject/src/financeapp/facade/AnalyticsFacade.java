package financeapp.facade;

import financeapp.model.Operation;
import financeapp.model.OperationType;

import java.util.*;

public class AnalyticsFacade {
    private OperationFacade operationFacade;

    public AnalyticsFacade(OperationFacade operationFacade) {
        this.operationFacade = operationFacade;
    }

    // Подсчёт разницы между доходами и расходами за период
    public double computeIncomeExpenseDifference(Date start, Date end) {
        double income = 0, expense = 0;
        for (Operation op : operationFacade.getAllOperations()) {
            if (op.getDate().compareTo(start) >= 0 && op.getDate().compareTo(end) <= 0) {
                if (op.getType() == OperationType.INCOME) {
                    income += op.getAmount();
                } else {
                    expense += op.getAmount();
                }
            }
        }
        return income - expense;
    }

    // Группировка операций по категориям (ключ – UUID категории)
    public Map<UUID, List<Operation>> groupOperationsByCategory(Date start, Date end) {
        Map<UUID, List<Operation>> map = new HashMap<>();
        for (Operation op : operationFacade.getAllOperations()) {
            if (op.getDate().compareTo(start) >= 0 && op.getDate().compareTo(end) <= 0) {
                map.computeIfAbsent(op.getCategoryId(), k -> new ArrayList<>()).add(op);
            }
        }
        return map;
    }
}