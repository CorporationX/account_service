package faang.school.accountservice.exception;

import java.util.NoSuchElementException;

public class AccountNumberNotFoundException extends NoSuchElementException {
    public AccountNumberNotFoundException(String message) {
        super(message);
    }
}
