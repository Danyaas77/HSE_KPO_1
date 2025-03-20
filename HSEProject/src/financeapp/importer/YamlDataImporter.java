package financeapp.importer;

import financeapp.facade.BankAccountFacade;
import financeapp.facade.CategoryFacade;
import financeapp.facade.OperationFacade;
import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.FinanceData;
import financeapp.model.CategoryType;
import financeapp.model.Operation;
import financeapp.model.OperationType;

import java.text.SimpleDateFormat;
import java.util.*;

public class YamlDataImporter extends ImporterBase {

    public YamlDataImporter(BankAccountFacade bankAccountFacade,
                            CategoryFacade categoryFacade,
                            OperationFacade operationFacade) {
        super(bankAccountFacade, categoryFacade, operationFacade);
    }

    @Override
    protected FinanceData parse(String fileContent) {
        String[] lines = fileContent.split("\\r?\\n");
        Map<String, List<String>> sections = new HashMap<>();
        String currentSection = null;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (!line.startsWith(" ") && line.trim().endsWith(":")) {
                currentSection = line.trim().replace(":", "");
                sections.put(currentSection, new ArrayList<>());
            } else if (line.trim().startsWith("-")) {
                sections.get(currentSection).add(line.trim().substring(1).trim());
            } else {
                List<String> list = sections.get(currentSection);
                if (list != null && !list.isEmpty()) {
                    int last = list.size() - 1;
                    list.set(last, list.get(last) + " " + line.trim());
                }
            }
        }

        List<BankAccount> bankAccounts = parseBankAccounts(sections.get("bankAccounts"));
        List<Category> categories = parseCategories(sections.get("categories"));
        List<Operation> operations = parseOperations(sections.get("operations"));
        return new FinanceData(bankAccounts, categories, operations);
    }

    private List<BankAccount> parseBankAccounts(List<String> objects) {
        List<BankAccount> accounts = new ArrayList<>();
        if (objects == null) return accounts;
        for (String obj : objects) {
            Map<String, String> map = parseYamlObject(obj);
            UUID id = safeParseUUID(map.get("id"));
            String name = map.getOrDefault("name", "");
            double balance = safeParseDouble(map.get("balance"));
            accounts.add(new BankAccount(id, name, balance));
        }
        return accounts;
    }

    private List<Category> parseCategories(List<String> objects) {
        List<Category> categories = new ArrayList<>();
        if (objects == null) return categories;
        for (String obj : objects) {
            Map<String, String> map = parseYamlObject(obj);
            UUID id = safeParseUUID(map.get("id"));
            String name = map.getOrDefault("name", "");
            String catType = map.getOrDefault("catType", "");
            CategoryType type = catType.equalsIgnoreCase("Income") ? CategoryType.INCOME : CategoryType.EXPENSE;
            categories.add(new Category(id, type, name));
        }
        return categories;
    }

    private List<Operation> parseOperations(List<String> objects) {
        List<Operation> operations = new ArrayList<>();
        if (objects == null) return operations;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (String obj : objects) {
            Map<String, String> map = parseYamlObject(obj);
            UUID id = safeParseUUID(map.get("id"));
            String opType = map.getOrDefault("opType", "");
            OperationType type = opType.equalsIgnoreCase("Income") ? OperationType.INCOME : OperationType.EXPENSE;
            double amount = safeParseDouble(map.get("amount"));
            UUID bankAccountId = safeParseUUID(map.get("bankAccountId"));
            Date date;
            try {
                date = dateFormat.parse(map.getOrDefault("date", "1970-01-01"));
            } catch (Exception e) {
                date = new Date();
            }
            String description = map.getOrDefault("description", "");
            UUID categoryId = safeParseUUID(map.get("categoryId"));
            operations.add(new Operation(id, type, bankAccountId, amount, date, description, categoryId));
        }
        return operations;
    }

    // Наивный парсер YAML-объекта из строки (предполагает формат: key: value, разделенные пробелами)
    private Map<String, String> parseYamlObject(String obj) {
        Map<String, String> map = new HashMap<>();
        String[] parts = obj.split(" ");
        for (String part : parts) {
            if (part.contains(":")) {
                String[] keyValue = part.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    // Вспомогательный метод для безопасного парсинга UUID
    private UUID safeParseUUID(String idStr) {
        if (idStr == null || idStr.isEmpty()) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }

    // Вспомогательный метод для безопасного парсинга double
    private double safeParseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}