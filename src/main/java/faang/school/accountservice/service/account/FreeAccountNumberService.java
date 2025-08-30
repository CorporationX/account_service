package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;

import java.util.List;
import java.util.function.Consumer;

public interface FreeAccountNumberService {

    void generateAccountNumbers(AccountType accountType, int batchSize);

    void retrieveAccountNumbers(AccountType type, int batchSize, Consumer<List<FreeAccountNumber>> consumer);
}
