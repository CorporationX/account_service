package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {

    String generateNumberByType(AccountType type);

    void generateNumberAndExecute(AccountType type, Consumer<FreeAccountNumber> operation);

    void saveNumber(AccountType type, String accountNumber);

    void generateNumbers(AccountType type, int count);

    void ensureMinimumNumbers(AccountType type, int requiredCount);
}
