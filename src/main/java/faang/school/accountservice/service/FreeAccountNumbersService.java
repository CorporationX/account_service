package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.FreeAccountNumberDto;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;

import java.util.List;
import java.util.function.Consumer;

public interface FreeAccountNumbersService {

    String generateNumberByType(AccountType type);

    void generateNumberAndExecute(AccountType type, Consumer<FreeAccountNumber> operation);

    void saveNumber(AccountType type, String accountNumber);

    void createFreeAccountNumbers(FreeAccountNumberDto freeAccountNumberDto);

    void saveNumbers(AccountType type, List<String> freeAccountNumbers);
}
