package faang.school.accountservice.service;

import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountType;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {
    FreeAccountNumber addFreeAccountNumber(AccountType accountType, boolean saveToDatabase);
    void getFreeAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> action);
}
