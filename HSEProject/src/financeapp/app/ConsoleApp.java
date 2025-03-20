package financeapp.app;

import financeapp.facade.AnalyticsFacade;
import financeapp.facade.BankAccountFacade;
import financeapp.facade.CategoryFacade;
import financeapp.facade.OperationFacade;
import financeapp.factory.DomainObjectFactory;
import financeapp.importer.CSVDataImporter;
import financeapp.importer.ImporterBase;
import financeapp.importer.JsonDataImporter;
import financeapp.importer.YamlDataImporter;
import financeapp.model.*;
import financeapp.visitor.ExportService;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Инициализация фасадов
        BankAccountFacade bankAccountFacade = new BankAccountFacade();
        CategoryFacade categoryFacade = new CategoryFacade();
        OperationFacade operationFacade = new OperationFacade(bankAccountFacade, categoryFacade);
        AnalyticsFacade analyticsFacade = new AnalyticsFacade(operationFacade);

        // Автоматическое определение путей к файлам (файлы в корне проекта)
        String projectRoot = System.getProperty("user.dir");
        String csvFilePath = projectRoot + File.separator + "data.csv";
        String jsonFilePath = projectRoot + File.separator + "data.json";
        String yamlFilePath = projectRoot + File.separator + "data.yaml";

        printBanner();

        while (true) {
            printMenu();
            System.out.print("Ваш выбор: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createBankAccount(bankAccountFacade);
                    break;
                case "2":
                    createCategory(categoryFacade);
                    break;
                case "3":
                    createOperation(bankAccountFacade, categoryFacade, operationFacade);
                    break;
                case "4":
                    computeAnalytics(analyticsFacade);
                    break;
                case "5":
                    exportData(bankAccountFacade, categoryFacade, operationFacade);
                    break;
                case "6":
                    importDataMenu(bankAccountFacade, categoryFacade, operationFacade,
                            csvFilePath, jsonFilePath, yamlFilePath);
                    break;
                case "0":
                    System.out.println("\nСпасибо за использование приложения \"Продвинутый учет финансов!\"!");
                    return;
                default:
                    System.out.println("Неверный выбор. Повторите попытку.");
                    break;
            }
        }
    }

    // ===== Методы создания/добавления =====

    private static void createBankAccount(BankAccountFacade bankAccountFacade) {
        System.out.println("\n=== Создание нового счета ===");
        String name = readString("Введите название счета: ");
        double balance = readDouble("Введите начальный баланс: ");
        BankAccount account = DomainObjectFactory.createBankAccount(name, balance);
        bankAccountFacade.addAccount(account);
        System.out.println("Счет создан: " + account);
    }

    private static void createCategory(CategoryFacade categoryFacade) {
        System.out.println("\n=== Создание новой категории ===");
        String categoryName = readString("Введите название категории: ");
        CategoryType catType = readCategoryType("Введите тип категории (INCOME/EXPENSE): ");
        Category category = DomainObjectFactory.createCategory(catType, categoryName);
        categoryFacade.addCategory(category);
        System.out.println("Категория создана: " + category);
    }

    private static void createOperation(BankAccountFacade bankAccountFacade,
                                        CategoryFacade categoryFacade,
                                        OperationFacade operationFacade) {
        System.out.println("\n=== Добавление операции ===");
        UUID accountId = readUUID("Введите id счета (UUID): ");
        BankAccount selectedAccount = bankAccountFacade.getAccount(accountId);
        if (selectedAccount == null) {
            System.out.println("Счет с таким id не найден. Операция отменена.");
            return;
        }
        UUID catId = readUUID("Введите id категории (UUID): ");
        Category selectedCategory = categoryFacade.getCategory(catId);
        if (selectedCategory == null) {
            System.out.println("Категория с таким id не найдена. Операция отменена.");
            return;
        }
        OperationType opType = readOperationType("Введите тип операции (INCOME/EXPENSE): ");
        double amount = readDouble("Введите сумму операции: ");
        String description = readString("Введите описание операции (можно оставить пустым): ");
        Date now = new Date();
        Operation operation = DomainObjectFactory.createOperation(
                opType, selectedAccount, amount, now, description, selectedCategory
        );
        operationFacade.addOperation(operation);
        System.out.println("Операция добавлена: " + operation);
    }

    // ===== Аналитика =====

    private static void computeAnalytics(AnalyticsFacade analyticsFacade) {
        System.out.println("\n=== Аналитика доходов и расходов ===");
        Date startDate = readDate("Введите начальную дату (yyyy-MM-dd): ");
        Date endDate = readDate("Введите конечную дату (yyyy-MM-dd): ");
        double diff = analyticsFacade.computeIncomeExpenseDifference(startDate, endDate);
        System.out.println("Разница доходов и расходов: " + diff);
    }

    // ===== Экспорт =====

    private static void exportData(BankAccountFacade baf,
                                   CategoryFacade cf,
                                   OperationFacade of) {
        System.out.println("\n=== Экспорт данных в CSV ===");
        List<BankAccount> accounts = new ArrayList<>(baf.getAllAccounts());
        List<Category> categories = new ArrayList<>(cf.getAllCategories());
        List<Operation> operations = new ArrayList<>(of.getAllOperations());
        String exportFilePath = readString("Введите путь для сохранения CSV файла (по умолчанию: ./export.csv): ");
        if (exportFilePath.isEmpty()) {
            exportFilePath = "export.csv";
        }
        ExportService.exportAllData(accounts, categories, operations, exportFilePath);
        System.out.println("Данные экспортированы в файл: " + exportFilePath);
    }

    // ===== Импорт =====

    private static void importDataMenu(BankAccountFacade baf,
                                       CategoryFacade cf,
                                       OperationFacade of,
                                       String csvFilePath,
                                       String jsonFilePath,
                                       String yamlFilePath) {
        System.out.println("\n=== Импорт данных ===");
        System.out.println("1. CSV");
        System.out.println("2. JSON");
        System.out.println("3. YAML");
        String fileTypeChoice = readString("Выберите формат файла: ");

        ImporterBase importer = null;
        String filePath = null;
        switch (fileTypeChoice) {
            case "1":
                importer = new CSVDataImporter(baf, cf, of);
                filePath = csvFilePath;
                break;
            case "2":
                importer = new JsonDataImporter(baf, cf, of);
                filePath = jsonFilePath;
                break;
            case "3":
                importer = new YamlDataImporter(baf, cf, of);
                filePath = yamlFilePath;
                break;
            default:
                System.out.println("Неверный выбор типа файла для импорта.");
                return;
        }

        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("Файл не найден: " + f.getAbsolutePath());
            return;
        }
        try {
            importer.importData(filePath);
            // После импорта выводим обновленные данные
            printAllData(baf, cf, of);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте: " + e.getMessage());
        }
    }

    // Вывод всех данных
    private static void printAllData(BankAccountFacade baf,
                                     CategoryFacade cf,
                                     OperationFacade of) {
        System.out.println("\n--- Текущие данные ---");
        System.out.println("\nСчета:");
        for (BankAccount ba : baf.getAllAccounts()) {
            System.out.println("  " + ba);
        }
        System.out.println("\nКатегории:");
        for (Category c : cf.getAllCategories()) {
            System.out.println("  " + c);
        }
        System.out.println("\nОперации:");
        for (Operation op : of.getAllOperations()) {
            System.out.println("  " + op);
        }
        System.out.println("--------------------------");
    }

    // ===== Вспомогательные методы чтения данных с повторными попытками =====

    // Чтение строки (простой вариант)
    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    // Чтение double с повторными попытками
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число!");
            }
        }
    }

    // Чтение даты с повторными попытками
    private static Date readDate(String prompt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return sdf.parse(line);
            } catch (ParseException e) {
                System.out.println("Ошибка: неверный формат даты (yyyy-MM-dd). Повторите ввод.");
            }
        }
    }

    // Чтение UUID с повторными попытками
    private static UUID readUUID(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return UUID.fromString(line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный формат UUID. Повторите ввод.");
            }
        }
    }

    // Чтение типа операции (INCOME/EXPENSE)
    private static OperationType readOperationType(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toUpperCase();
            try {
                return OperationType.valueOf(line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный тип. Введите INCOME или EXPENSE.");
            }
        }
    }

    // Чтение типа категории (INCOME/EXPENSE)
    private static CategoryType readCategoryType(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toUpperCase();
            try {
                return CategoryType.valueOf(line);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный тип. Введите INCOME или EXPENSE.");
            }
        }
    }

    // ===== Красивое меню и баннер =====

    private static void printBanner() {
        System.out.println("================================================");
        System.out.println("=          Добро пожаловать в систему          =");
        System.out.println("=        \"Продвинутый учет финансов!\"            =");
        System.out.println("================================================");
    }

    private static void printMenu() {
        System.out.println("\n============================================");
        System.out.println("|                ГЛАВНОЕ МЕНЮ               |");
        System.out.println("============================================");
        System.out.println("| 1. Создать счет                           |");
        System.out.println("| 2. Создать категорию                      |");
        System.out.println("| 3. Добавить операцию                      |");
        System.out.println("| 4. Аналитика: разница доходов/расходов    |");
        System.out.println("| 5. Экспорт данных в CSV                   |");
        System.out.println("| 6. Импорт данных из файла (CSV/JSON/YAML) |");
        System.out.println("| 0. Выход                                  |");
        System.out.println("============================================");
    }
}