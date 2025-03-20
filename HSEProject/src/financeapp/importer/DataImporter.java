package financeapp.importer;

public abstract class DataImporter {
    public final void importData(String filePath) {
        String data = readFile(filePath);
        parseData(data);
        // Возможна общая обработка данных после импорта
    }

    private String readFile(String filePath) {
        // В реальном приложении читаем данные из файла
        System.out.println("Reading file: " + filePath);
        return "data from " + filePath;
    }

    protected abstract void parseData(String data);
}