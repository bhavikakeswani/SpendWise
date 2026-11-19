package SpendWise;


import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CSVFileManager {

    private final String transactionFile = "data/transactions.csv";
    private final String budgetFile = "data/budgets.csv";

    public CSVFileManager() throws IOException {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        createFile(transactionFile,
                "id,type,amount,details,date,description");

        createFile(budgetFile, "month,limit");
    }

    private void createFile(String path, String header)
            throws IOException {

        File file = new File(path);

        if (!file.exists()) {
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                writer.newLine();
            }
        }
    }

    private String escape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private List<String> parseCSV(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (insideQuotes && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (ch == ',' && !insideQuotes) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(ch);
            }
        }

        values.add(value.toString());
        return values;
    }

    public void saveTransactions(List<Transaction> transactions)
            throws IOException {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(transactionFile))) {

            writer.write("id,type,amount,details,date,description");
            writer.newLine();

            for (Transaction transaction : transactions) {

                String line =
                        transaction.getId() + "," +
                        transaction.getType() + "," +
                        transaction.getAmount() + "," +
                        escape(transaction.getDetails()) + "," +
                        transaction.getDate() + "," +
                        escape(transaction.getDescription());

                writer.write(line);
                writer.newLine();
            }
        }
    }

    public List<Transaction> loadTransactions()
            throws IOException {

        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(transactionFile))) {

            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    List<String> data = parseCSV(line);

                    int id = Integer.parseInt(data.get(0));
                    String type = data.get(1);
                    BigDecimal amount = new BigDecimal(data.get(2));
                    String details = data.get(3);
                    LocalDate date = LocalDate.parse(data.get(4));
                    String description = data.get(5);

                    if (type.equals("INCOME")) {
                        transactions.add(
                                new Income(id, amount, details,
                                        description, date));
                    } else if (type.equals("EXPENSE")) {
                        transactions.add(
                                new Expense(id, amount,
                                        Category.valueOf(details),
                                        description, date));
                    }

                } catch (RuntimeException e) {
                    System.out.println(
                            "Skipping invalid transaction record.");
                }
            }
        }

        return transactions;
    }

    public void saveBudget(List<Budget> budgets)
            throws IOException {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(budgetFile))) {

            writer.write("month,limit");
            writer.newLine();

            for (Budget budget : budgets) {
                writer.write(
                        budget.getMonth() + "," + budget.getLimit());
                writer.newLine();
            }
        }
    }

    public List<Budget> loadBudget() throws IOException {

        List<Budget> budgets = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(budgetFile))) {

            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] data = line.split(",");

                    YearMonth month = YearMonth.parse(data[0]);
                    BigDecimal limit = new BigDecimal(data[1]);

                    budgets.add(new Budget(month, limit));

                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid budget record.");
                }
            }
        }

        return budgets;
    }
}
