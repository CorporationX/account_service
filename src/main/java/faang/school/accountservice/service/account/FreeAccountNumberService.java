package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;

import java.util.function.Consumer;

public interface FreeAccountNumberService {

    void generateAccountNumbers(AccountType accountType, int batchSize);

    void retrieveAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> consumer);
}
