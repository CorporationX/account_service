package faang.school.accountservice.service;

import faang.school.accountservice.dto.FreeAccountNumberDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;

import java.util.function.Consumer;

public interface FreeAccountNumbersService {

    void retrieveFreeAccountNumber(AccountType type, Consumer<FreeAccountNumber> consumer);

    FreeAccountNumberDto generateAccountNumber(AccountType type);
}
