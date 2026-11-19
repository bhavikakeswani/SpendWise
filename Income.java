package SpendWise;


import java.math.BigDecimal;
import java.time.LocalDate;

public class Income extends Transaction {

    private String source;

    public Income(int id, BigDecimal amount,
                  String source, String description,
                  LocalDate date) {

        super(id, amount, description, date);
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getType() {
        return "INCOME";
    }

    @Override
    public String getDetails() {
        return source;
    }
}
