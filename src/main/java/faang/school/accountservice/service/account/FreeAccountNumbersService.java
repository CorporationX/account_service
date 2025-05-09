package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.FreeAccountNumberDto;

import java.util.function.Function;

public interface FreeAccountNumbersService {
    FreeAccountNumberDto addFreeAccountNumber(String accountType, String accountNumber);

    <R> R withNewAccountNumber(String accountType, Function<String, R> action, String prefix, int totalLength);
}
