package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {

    void generateAccountNumbers(AccountType type, int batchSize);

    void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer);

}