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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVDataImporter extends ImporterBase {

    public CSVDataImporter(BankAccountFacade bankAccountFacade,
                           CategoryFacade categoryFacade,
                           OperationFacade operationFacade) {
        super(bankAccountFacade, categoryFacade, operationFacade);
    }

    @Override
    protected FinanceData parse(String fileContent) {
        String processedContent = preprocessCsvContent(fileContent);
        List<BankAccount> bankAccounts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String[] lines = processedContent.split("\\r?\\n");
        if (lines.length == 0) return new FinanceData(bankAccounts, categories, operations);

        String headerLine = lines[0];
        String[] headers = headerLine.split(";");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            String[] values = line.split(";");
            Map<String, String> record = new HashMap<>();
            for (int j = 0; j < headers.length && j < values.length; j++) {
                record.put(headers[j].trim(), values[j].trim());
            }
            String recordType = record.getOrDefault("RecordType", record.get("Type"));
            if (recordType == null || recordType.isEmpty()) continue;

            if (recordType.equalsIgnoreCase("BankAccount")) {
                UUID id = parseUUID(record.get("id"));
                String name = record.getOrDefault("name", "");
                double balance = parseDouble(record.get("balance"));
                bankAccounts.add(new BankAccount(id, name, balance));
            } else if (recordType.equalsIgnoreCase("Category")) {
                UUID id = parseUUID(record.get("id"));
                String name = record.getOrDefault("name", "");
                String catType = record.getOrDefault("catType", "");
                CategoryType type = catType.equalsIgnoreCase("Income") ? CategoryType.INCOME : CategoryType.EXPENSE;
                categories.add(new Category(id, type, name));
            } else if (recordType.equalsIgnoreCase("Operation")) {
                UUID id = parseUUID(record.get("id"));
                String opTypeStr = record.getOrDefault("opType", "");
                OperationType opType = opTypeStr.equalsIgnoreCase("Income") ? OperationType.INCOME : OperationType.EXPENSE;
                double amount = parseDouble(record.get("amount"));
                if (opType == OperationType.EXPENSE) {
                    amount = Math.abs(amount);
                }
                UUID bankAccountId = parseUUID(record.get("bankAccountId"));
                Date date = parseDate(record.get("date"), dateFormat);
                String description = record.getOrDefault("description", "");
                UUID categoryId = parseUUID(record.get("categoryId"));
                operations.add(new Operation(id, opType, bankAccountId, amount, date, description, categoryId));
            }
        }
        return new FinanceData(bankAccounts, categories, operations);
    }

    private String preprocessCsvContent(String content) {
        String[] lines = content.split("\\r?\\n");
        List<String> processedLines = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            processedLines.add(line.replaceAll(";+$", ""));
        }
        return String.join(System.lineSeparator(), processedLines);
    }

    private UUID parseUUID(String idStr) {
        if (idStr == null || idStr.isEmpty()) return UUID.randomUUID();
        try {
            return UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }

    private double parseDouble(String value) {
        if (value == null || value.isEmpty()) return 0.0;
        try {
            return NumberFormat.getInstance(Locale.ENGLISH).parse(value).doubleValue();
        } catch (ParseException e) {
            return 0.0;
        }
    }

    private Date parseDate(String dateStr, SimpleDateFormat dateFormat) {
        if (dateStr == null || dateStr.isEmpty()) return new Date();
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }
}