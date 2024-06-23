package faang.school.accountservice.service.account_number;

import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountType;

import java.util.function.Consumer;

public interface AccountNumberService {

    void getUniqueAccountNumber(Consumer<FreeAccountNumber> action, AccountType accountType);

    FreeAccountNumber generateAccountNumber(AccountType accountType);
}
