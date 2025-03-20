package financeapp.importer;

import financeapp.facade.BankAccountFacade;
import financeapp.facade.CategoryFacade;
import financeapp.facade.OperationFacade;
import financeapp.model.BankAccount;
import financeapp.model.Category;
import financeapp.model.FinanceData;
import financeapp.model.Operation;

public abstract class ImporterBase {
    protected BankAccountFacade bankAccountFacade;
    protected CategoryFacade categoryFacade;
    protected OperationFacade operationFacade;

    public ImporterBase(BankAccountFacade bankAccountFacade,
                        CategoryFacade categoryFacade,
                        OperationFacade operationFacade) {
        this.bankAccountFacade = bankAccountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
    }

    public void importData(String filePath) {
        try {
            String fileContent = java.nio.file.Files.readString(java.nio.file.Paths.get(filePath));
            FinanceData financeData = parse(fileContent);

            // Добавляем данные через фасады
            for (BankAccount account : financeData.getBankAccounts()) {
                bankAccountFacade.addAccount(account);
            }
            for (Category category : financeData.getCategories()) {
                categoryFacade.addCategory(category);
            }
            for (Operation operation : financeData.getOperations()) {
                operationFacade.addOperation(operation);
            }
            System.out.println("Импорт успешно завершён!");
        } catch (Exception e) {
            System.err.println("Ошибка при импорте: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected abstract FinanceData parse(String fileContent);
}