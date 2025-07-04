package faang.school.accountservice.exception.balance;

import java.math.BigDecimal;
import java.util.UUID;

public class NotEnoughAvailableFundsException extends RuntimeException {
    public NotEnoughAvailableFundsException(UUID balanceId, BigDecimal requested, BigDecimal available) {
        super("Not enough funds in balance " + balanceId + ": requested " + requested + ", available " + available);
    }
}
