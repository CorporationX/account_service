package faang.school.accountservice.exception.account;

import faang.school.accountservice.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class AccountHasBeenUpdateException extends ApiException {
    private static final String MESSAGE = "Account id=%s has been update. Reload information";

    public AccountHasBeenUpdateException(UUID id) {
        super(MESSAGE, HttpStatus.CONFLICT, id);
    }
}
