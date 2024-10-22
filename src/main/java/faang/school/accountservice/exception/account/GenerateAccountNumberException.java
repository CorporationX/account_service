package faang.school.accountservice.exception.account;

import faang.school.accountservice.enums.account.Type;
import faang.school.accountservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class GenerateAccountNumberException extends ApiException {
    private static final String MESSAGE = "Account type [%s] does not support number generation";

    public GenerateAccountNumberException(Type accountType) {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, accountType);
    }
}
