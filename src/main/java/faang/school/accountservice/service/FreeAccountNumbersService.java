package faang.school.accountservice.service;

import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountType;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {
    void getUniqueAccountNumber(Consumer<FreeAccountNumber> accountConsumer, AccountType accountType);

    FreeAccountNumber generateUniqueAccountNumber(AccountType accountType);

    void generateMultipleUniqueAccountNumbers(int quantity, AccountType accountType);

    void generateLimitedUniqueAccountNumbers(int limit, AccountType accountType);

}
