package faang.school.accountservice.service.balance.transactionHandler;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;

public interface TransactionHandler {
    TransactionType getType();

    Balance handle(TransactionDto transaction, Account account);
}
