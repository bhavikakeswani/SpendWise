package SpendWise;


import java.math.BigDecimal;
import java.time.YearMonth;

public class Budget {

    private YearMonth month;
    private BigDecimal limit;

    public Budget(YearMonth month, BigDecimal limit) {
        this.month = month;
        this.limit = limit;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getRemainingBudget(BigDecimal expenses) {
        return limit.subtract(expenses);
    }

    public boolean isExceeded(BigDecimal expenses) {
        return expenses.compareTo(limit) > 0;
    }
}
