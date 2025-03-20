package financeapp.importer;

import financeapp.importer.CSVDataImporter;
import financeapp.importer.JsonDataImporter;
import financeapp.importer.YamlDataImporter;
import financeapp.facade.BankAccountFacade;
import financeapp.facade.CategoryFacade;
import financeapp.facade.OperationFacade;
import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.Operation;

public class DataImportApp {
    public static void main(String[] args) {
        // Создаем фасады для наших доменных объектов
        BankAccountFacade bankAccountFacade = new BankAccountFacade();
        CategoryFacade categoryFacade = new CategoryFacade();
        OperationFacade operationFacade = new OperationFacade(bankAccountFacade, categoryFacade);

        // Пример использования CSV-импортёра
        CSVDataImporter csvImporter = new CSVDataImporter(bankAccountFacade, categoryFacade, operationFacade);
        String csvFilePath = "financeapp/Data/data.csv";
        csvImporter.importData(csvFilePath);

        System.out.println("Импортированные счета:");
        for (BankAccount account : bankAccountFacade.getAllAccounts()) {
            System.out.println(account);
        }
        System.out.println("\nИмпортированные категории:");
        for (Category category : categoryFacade.getAllCategories()) {
            System.out.println(category);
        }
        System.out.println("\nИмпортированные операции:");
        for (Operation operation : operationFacade.getAllOperations()) {
            System.out.println(operation);
        }

        // Пример использования JSON-импортёра
        JsonDataImporter jsonImporter = new JsonDataImporter(bankAccountFacade, categoryFacade, operationFacade);
        jsonImporter.importData("financeapp/Data/data.json");

        // Пример использования YAML-импортёра
        YamlDataImporter yamlImporter = new YamlDataImporter(bankAccountFacade, categoryFacade, operationFacade);
        yamlImporter.importData("financeapp/Data/data.yaml");
    }
}