package faang.school.accountservice.exception.balance;

import faang.school.accountservice.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class BalanceHasBeenUpdatedException extends ApiException {
    private static final String MESSAGE_TEMPLATE = "Balance id=%s has been update. Reload information";

    public BalanceHasBeenUpdatedException(UUID id) {
        super(MESSAGE_TEMPLATE, HttpStatus.CONFLICT, id);
    }
}
