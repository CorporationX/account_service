package faang.school.accountservice.exception.balance;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BalanceHasBeenUpdatedException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Balance with id=%s has been updated. Reload information.";
    private final HttpStatus httpStatus;

    public BalanceHasBeenUpdatedException(Long balanceId) {
        super(String.format(MESSAGE_TEMPLATE, balanceId));
        this.httpStatus = HttpStatus.CONFLICT;
    }
}
