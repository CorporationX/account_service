package faang.school.accountservice.exception.account;

import faang.school.accountservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends ApiException {
    private static final String MESSAGE = "Account id=%s not found";

    public AccountNotFoundException(Long id) {
        super(MESSAGE, HttpStatus.NOT_FOUND, id);
    }
}
