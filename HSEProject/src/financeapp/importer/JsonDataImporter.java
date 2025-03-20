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

public class JsonDataImporter extends ImporterBase {

    public JsonDataImporter(BankAccountFacade bankAccountFacade,
                            CategoryFacade categoryFacade,
                            OperationFacade operationFacade) {
        super(bankAccountFacade, categoryFacade, operationFacade);
    }

    @Override
    protected FinanceData parse(String fileContent) {
        // Очень наивная реализация парсинга JSON, рассчитанная на строго фиксированный формат.
        String json = fileContent.replaceAll("[\\r\\n]", "").trim();
        String bankAccountsArray = extractJsonArray(json, "bankAccounts");
        String categoriesArray = extractJsonArray(json, "categories");
        String operationsArray = extractJsonArray(json, "operations");

        List<BankAccount> bankAccounts = parseBankAccounts(bankAccountsArray);
        List<Category> categories = parseCategories(categoriesArray);
        List<Operation> operations = parseOperations(operationsArray);
        return new FinanceData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private String extractJsonArray(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if(keyIndex < 0) return "";
        int start = json.indexOf('[', keyIndex);
        int end = json.indexOf(']', start);
        return (start >= 0 && end >= 0) ? json.substring(start + 1, end) : "";
    }

    private List<String> splitJsonObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        String[] parts = arrayContent.split("\\},");
        for(String part : parts) {
            part = part.trim();
            if(!part.endsWith("}")) {
                part += "}";
            }
            if(!part.isEmpty()){
                objects.add(part);
            }
        }
        return objects;
    }

    private Map<String, String> parseJsonObject(String jsonObject) {
        Map<String, String> map = new HashMap<>();
        jsonObject = jsonObject.substring(1, jsonObject.length()-1); // убираем {}
        String[] pairs = jsonObject.split(",");
        for(String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if(keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");
                map.put(key, value);
            }
        }
        return map;
    }

    private List<BankAccount> parseBankAccounts(String arrayContent) {
        List<BankAccount> accounts = new ArrayList<>();
        for(String obj : splitJsonObjects(arrayContent)) {
            Map<String, String> map = parseJsonObject(obj);
            UUID id = map.containsKey("id") ? UUID.fromString(map.get("id")) : UUID.randomUUID();
            String name = map.getOrDefault("name", "");
            double balance = Double.parseDouble(map.getOrDefault("balance", "0"));
            accounts.add(new BankAccount(id, name, balance));
        }
        return accounts;
    }

    private List<Category> parseCategories(String arrayContent) {
        List<Category> categories = new ArrayList<>();
        for(String obj : splitJsonObjects(arrayContent)) {
            Map<String, String> map = parseJsonObject(obj);
            UUID id = map.containsKey("id") ? UUID.fromString(map.get("id")) : UUID.randomUUID();
            String name = map.getOrDefault("name", "");
            String catType = map.getOrDefault("catType", "");
            CategoryType type = catType.equalsIgnoreCase("Income") ? CategoryType.INCOME : CategoryType.EXPENSE;
            categories.add(new Category(id, type, name));
        }
        return categories;
    }

    private List<Operation> parseOperations(String arrayContent) {
        List<Operation> operations = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for(String obj : splitJsonObjects(arrayContent)) {
            Map<String, String> map = parseJsonObject(obj);
            UUID id = map.containsKey("id") ? UUID.fromString(map.get("id")) : UUID.randomUUID();
            String opType = map.getOrDefault("opType", "");
            OperationType type = opType.equalsIgnoreCase("Income") ? OperationType.INCOME : OperationType.EXPENSE;
            double amount = Double.parseDouble(map.getOrDefault("amount", "0"));
            UUID bankAccountId = map.containsKey("bankAccountId") ? UUID.fromString(map.get("bankAccountId")) : null;
            Date date;
            try {
                date = dateFormat.parse(map.getOrDefault("date", "1970-01-01"));
            } catch(Exception e) {
                date = new Date();
            }
            String description = map.getOrDefault("description", "");
            UUID categoryId = map.containsKey("categoryId") ? UUID.fromString(map.get("categoryId")) : null;
            operations.add(new Operation(id, type, bankAccountId, amount, date, description, categoryId));
        }
        return operations;
    }
}