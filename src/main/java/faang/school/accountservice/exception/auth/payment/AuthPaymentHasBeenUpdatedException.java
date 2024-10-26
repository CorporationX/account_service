package faang.school.accountservice.exception.auth.payment;

import faang.school.accountservice.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class AuthPaymentHasBeenUpdatedException extends ApiException {
    private static final String MESSAGE_TEMPLATE = "AuthPayment id=%s has been update. Reload information";

    public AuthPaymentHasBeenUpdatedException(UUID id) {
        super(MESSAGE_TEMPLATE, HttpStatus.CONFLICT, id);
    }
}
