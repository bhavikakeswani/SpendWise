package SpendWise;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseManager {

    private ArrayList<Transaction> transactions;
    private Map<YearMonth, Budget> budgets;
    private CSVFileManager fileManager;

    public ExpenseManager() throws IOException {

        fileManager = new CSVFileManager();

        transactions = new ArrayList<>(
                fileManager.loadTransactions());

        budgets = new HashMap<>();

        for (Budget budget : fileManager.loadBudget()) {
            budgets.put(budget.getMonth(), budget);
        }
    }

    private int generateId() {
        int maxId = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getId() > maxId) {
                maxId = transaction.getId();
            }
        }

        return maxId + 1;
    }

    public void addIncome(BigDecimal amount, String source,
                          String description,
                          java.time.LocalDate date) throws IOException {

        Income income = new Income(
                generateId(), amount, source, description, date);

        transactions.add(income);
        saveTransactions();

        System.out.println("Income added successfully!");
    }

    public void addExpense(BigDecimal amount, Category category,
                           String description,
                           java.time.LocalDate date) throws IOException {

        Expense expense = new Expense(
                generateId(), amount, category, description, date);

        transactions.add(expense);
        saveTransactions();

        System.out.println("Expense added successfully!");
    }

    public void deleteTransaction(int id) throws IOException {

        boolean removed = transactions.removeIf(
                transaction -> transaction.getId() == id);

        if (removed) {
            saveTransactions();
            System.out.println("Transaction deleted successfully!");
        } else {
            System.out.println("Transaction not found!");
        }
    }

    public List<Transaction> getMonthlyTransactions(YearMonth month) {

        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactions) {

            if (YearMonth.from(transaction.getDate()).equals(month)) {
                result.add(transaction);
            }
        }

        return result;
    }

    public BigDecimal getTotalIncome(YearMonth month) {

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : getMonthlyTransactions(month)) {

            if (transaction instanceof Income) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getTotalExpenses(YearMonth month) {

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : getMonthlyTransactions(month)) {

            if (transaction instanceof Expense) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getBalance(YearMonth month) {

        return getTotalIncome(month)
                .subtract(getTotalExpenses(month));
    }

    public Map<Category, BigDecimal> getCategoryWiseExpenses(
            YearMonth month) {

        Map<Category, BigDecimal> result = new HashMap<>();

        for (Transaction transaction : getMonthlyTransactions(month)) {

            if (transaction instanceof Expense) {

                Expense expense = (Expense) transaction;

                result.put(
                        expense.getCategory(),
                        result.getOrDefault(
                                expense.getCategory(), BigDecimal.ZERO)
                                .add(expense.getAmount())
                );
            }
        }

        return result;
    }

    public Category getHighestExpenseCategory(YearMonth month) {

        Map<Category, BigDecimal> expenses =
                getCategoryWiseExpenses(month);

        Category highest = null;
        BigDecimal max = BigDecimal.ZERO;

        for (Map.Entry<Category, BigDecimal> entry
                : expenses.entrySet()) {

            if (entry.getValue().compareTo(max) > 0) {
                max = entry.getValue();
                highest = entry.getKey();
            }
        }

        return highest;
    }

    public void setBudget(YearMonth month, BigDecimal limit)
            throws IOException {

        Budget budget = budgets.get(month);

        if (budget == null) {
            budget = new Budget(month, limit);
            budgets.put(month, budget);
        } else {
            budget.setLimit(limit);
        }

        saveBudgets();

        System.out.println("Monthly budget saved successfully!");
    }

    public Budget getBudget(YearMonth month) {
        return budgets.get(month);
    }

    public void saveTransactions() throws IOException {
        fileManager.saveTransactions(transactions);
    }

    public void saveBudgets() throws IOException {
        fileManager.saveBudget(
                new ArrayList<>(budgets.values()));
    }
}
