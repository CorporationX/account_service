package faang.school.accountservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException(String message) {
        super(message);
        log.error(message);
    }
}
