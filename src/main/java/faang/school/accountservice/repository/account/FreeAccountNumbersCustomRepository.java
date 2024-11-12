package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.FreeAccountNumber;

import java.util.List;

public interface FreeAccountNumbersCustomRepository {
    void saveAllFreeAccountNumbersBatched(List<FreeAccountNumber> freeAccountNumbers);
}
