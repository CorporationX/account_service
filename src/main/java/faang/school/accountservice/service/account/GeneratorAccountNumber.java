package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.account.Type;

public interface GeneratorAccountNumber {
    Type getAccountType();

    String generateNumber();
}
