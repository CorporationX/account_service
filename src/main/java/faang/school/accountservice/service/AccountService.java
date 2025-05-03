package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountOpenRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.enums.OwnerType;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    void open(AccountOpenRequest request);

    AccountResponse get(String accountNumber);

    List<AccountResponse> get(Long ownerId, OwnerType ownerType);

    void block(String accountNumber);

    void unblock(String accountNumber);

    void close(String accountNumber);

    void delete(String accountNumber);

    void updateBalance(String accountNumber, BigDecimal amount);
}
