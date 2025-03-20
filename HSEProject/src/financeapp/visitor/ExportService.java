package financeapp.visitor;
import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.Operation;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExportService {

    public static void exportAllData(List<BankAccount> accounts, List<Category> categories,
                                     List<Operation> operations, String filePath) {
        ExportToCSVVisitor visitor = new ExportToCSVVisitor();

        // Экспорт данных с помощью паттерна Посетитель
        for (BankAccount account : accounts) {
            account.accept(visitor);
        }
        for (Category category : categories) {
            category.accept(visitor);
        }
        for (Operation operation : operations) {
            operation.accept(visitor);
        }

        // Получаем результирующую строку CSV
        String result = visitor.getResult();

        // Записываем результат в файл
        try {
            Files.write(Paths.get(filePath), result.getBytes());
            System.out.println("Данные успешно экспортированы в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи файла: " + e.getMessage());
        }
    }
}

