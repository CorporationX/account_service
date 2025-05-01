package faang.school.accountservice.service.account;

import java.util.function.Function;

public interface FreeAccountNumbersService {
    void addFreeAccountNumber(String accountType, String accountNumber);

    <R> R withNewAccountNumber(String accountType, Function<String, R> action, String prefix, int totalLength);
}
