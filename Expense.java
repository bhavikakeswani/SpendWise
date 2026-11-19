package SpendWise;


import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense extends Transaction {

    private Category category;

    public Expense(int id, BigDecimal amount,
                   Category category, String description,
                   LocalDate date) {

        super(id, amount, description, date);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }

    @Override
    public String getDetails() {
        return category.name();
    }
}
