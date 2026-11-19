package SpendWise;


import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Transaction {

    private int id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

    public Transaction(int id, BigDecimal amount,
                       String description, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public abstract String getType();

    public abstract String getDetails();

    public void displayDetails() {
        System.out.println("ID: " + id);
        System.out.println("Type: " + getType());
        System.out.println("Amount: Rs. " + amount);
        System.out.println("Details: " + getDetails());
        System.out.println("Description: " + description);
        System.out.println("Date: " + date);
    }
}
