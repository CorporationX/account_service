package faang.school.accountservice.service.account_number.generation;

import faang.school.accountservice.model.enums.AccountType;

public interface AccountNumberGenerationService {

    void fillFreeNumbersTo(int amount, AccountType accountType);
}
