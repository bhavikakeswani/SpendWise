package SpendWise;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static ExpenseManager manager;

    public static void main(String[] args) {

        try {
            manager = new ExpenseManager();

            System.out.println("\n==================================");
            System.out.println("          WELCOME TO SPENDWISE");
            System.out.println("       PERSONAL EXPENSE TRACKER");
            System.out.println("==================================");

            boolean running = true;

            while (running) {

                displayMenu();

                int choice = readInt("Enter your choice: ");

                try {
                    switch (choice) {

                        case 1:
                            addIncome();
                            break;

                        case 2:
                            addExpense();
                            break;

                        case 3:
                            viewTransactions();
                            break;

                        case 4:
                            deleteTransaction();
                            break;

                        case 5:
                            financialOverview();
                            break;

                        case 6:
                            manageBudget();
                            break;

                        case 7:
                            monthlyReports();
                            break;

                        case 8:
                            manager.saveTransactions();
                            manager.saveBudgets();

                            System.out.println(
                                    "\nThank you for using SpendWise!");

                            running = false;
                            break;

                        default:
                            System.out.println(
                                    "Invalid choice! Try again.");
                    }

                } catch (IOException e) {
                    System.out.println(
                            "Error saving data: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "Unable to initialize application: "
                            + e.getMessage());
        }

        sc.close();
    }

    private static void displayMenu() {

        System.out.println("\n==================================");
        System.out.println("            SPENDWISE");
        System.out.println("==================================");
        System.out.println("1. Add Income");
        System.out.println("2. Add Expense");
        System.out.println("3. View All Transactions");
        System.out.println("4. Delete Transaction");
        System.out.println("5. Financial Overview");
        System.out.println("6. Manage Monthly Budget");
        System.out.println("7. Monthly Reports");
        System.out.println("8. Exit");
        System.out.println("==================================");
    }

    private static int readInt(String message) {

        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static BigDecimal readAmount(String message) {

        while (true) {
            System.out.print(message);

            try {
                BigDecimal amount =
                        new BigDecimal(sc.nextLine().trim());

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println(
                            "Amount must be greater than zero.");
                    continue;
                }

                return amount;

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount.");
            }
        }
    }

    private static String readText(String message) {

        while (true) {
            System.out.print(message);

            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    private static LocalDate readDate() {

        while (true) {

            System.out.print(
                    "Enter date (YYYY-MM-DD) or press Enter for today: ");

            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                return LocalDate.now();
            }

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Invalid date. Please use YYYY-MM-DD.");
            }
        }
    }

    private static YearMonth readMonth() {

        while (true) {

            System.out.print(
                    "Enter month (YYYY-MM): ");

            try {
                return YearMonth.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Invalid month. Example: 2026-11");
            }
        }
    }

    private static void addIncome() throws IOException {

        System.out.println("\n---------- ADD INCOME ----------");

        BigDecimal amount = readAmount("Enter amount: Rs. ");
        String source = readText("Enter income source: ");
        String description = readText("Enter description: ");
        LocalDate date = readDate();

        manager.addIncome(amount, source, description, date);
    }

    private static void addExpense() throws IOException {

        System.out.println("\n---------- ADD EXPENSE ----------");

        BigDecimal amount = readAmount("Enter amount: Rs. ");

        System.out.println("\nExpense Categories:");

        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println(
                    (i + 1) + ". " + categories[i]);
        }

        int choice;

        while (true) {

            choice = readInt("Select category: ");

            if (choice >= 1 && choice <= categories.length) {
                break;
            }

            System.out.println("Invalid category selection.");
        }

        Category category = categories[choice - 1];

        String description = readText("Enter description: ");
        LocalDate date = readDate();

        manager.addExpense(
                amount, category, description, date);
    }

    private static void viewTransactions() {

        System.out.println("\n---------- TRANSACTIONS ----------");

        YearMonth month = readMonth();

        List<Transaction> transactions =
                manager.getMonthlyTransactions(month);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {

            System.out.println("\n--------------------------");
            transaction.displayDetails();
        }
    }

    private static void deleteTransaction()
            throws IOException {

        System.out.println("\n---------- DELETE TRANSACTION ----------");

        int id = readInt("Enter transaction ID: ");

        manager.deleteTransaction(id);
    }

    private static void financialOverview() {

        System.out.println("\n---------- FINANCIAL OVERVIEW ----------");

        YearMonth month = readMonth();

        BigDecimal income = manager.getTotalIncome(month);
        BigDecimal expenses = manager.getTotalExpenses(month);
        BigDecimal balance = manager.getBalance(month);

        System.out.println("\nMonth: " + month);
        System.out.println("----------------------------------");
        System.out.println("Total Income: Rs. " + income);
        System.out.println("Total Expenses: Rs. " + expenses);
        System.out.println("Available Balance: Rs. " + balance);

        Budget budget = manager.getBudget(month);

        if (budget != null) {

            BigDecimal remaining =
                    budget.getRemainingBudget(expenses);

            System.out.println("Monthly Budget: Rs. "
                    + budget.getLimit());

            System.out.println("Remaining Budget: Rs. "
                    + remaining);

            if (budget.isExceeded(expenses)) {
                System.out.println("WARNING: Budget exceeded!");
            } else if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("Monthly budget fully used.");
            } else {
                System.out.println("You are within your budget.");
            }

        } else {
            System.out.println("No budget set for this month.");
        }

        System.out.println("----------------------------------");
    }

    private static void manageBudget() throws IOException {

        System.out.println("\n---------- MANAGE BUDGET ----------");

        YearMonth month = readMonth();

        BigDecimal limit =
                readAmount("Enter monthly budget: Rs. ");

        manager.setBudget(month, limit);
    }

    private static void monthlyReports() {

        System.out.println("\n---------- MONTHLY REPORT ----------");

        YearMonth month = readMonth();

        BigDecimal income = manager.getTotalIncome(month);
        BigDecimal expenses = manager.getTotalExpenses(month);

        System.out.println("\nReport for: " + month);
        System.out.println("----------------------------------");

        System.out.println("Total Income: Rs. " + income);
        System.out.println("Total Expenses: Rs. " + expenses);
        System.out.println("Savings: Rs. "
                + income.subtract(expenses));

        System.out.println("\nCategory-wise Expenses:");

        Map<Category, BigDecimal> categoryExpenses =
                manager.getCategoryWiseExpenses(month);

        if (categoryExpenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            for (Map.Entry<Category, BigDecimal> entry
                    : categoryExpenses.entrySet()) {

                System.out.println(
                        entry.getKey() + " : Rs. "
                                + entry.getValue());
            }

            Category highest =
                    manager.getHighestExpenseCategory(month);

            System.out.println("\nHighest Spending Category: "
                    + highest);
        }

        System.out.println("----------------------------------");
    }
}
