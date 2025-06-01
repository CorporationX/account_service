package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountNumberType;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {

    void generateAccountNumbers(AccountNumberType type, int batchSize);

    void receiveAccountNumber(AccountNumberType type, Consumer<FreeAccountNumber> numberConsumer);
}
