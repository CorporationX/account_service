package faang.school.accountservice.service;

import java.util.function.Consumer;

public interface FreeAccountNumberService {

    void generateAndSaveFreeAccountNumbers(String type, int count);

    String getFreeAccountNumber(String accountType, Consumer<String> accountCreation);

    long countByAccountType(String accountType);
}
