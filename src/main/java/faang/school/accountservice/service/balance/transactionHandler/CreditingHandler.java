package faang.school.accountservice.service.balance.transactionHandler;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;

public class CreditingHandler implements TransactionHandler {

    @Override
    public TransactionType getType() {
        return TransactionType.CREDITING;
    }

    @Override
    public Balance handle(TransactionDto transaction, Account account) {
        Balance balance = account.getBalance();
        balance.setActualBalance(balance.getActualBalance().add(transaction.getAmount()));
        balance.setPaymentNumber(transaction.getPaymentNumber());
        return balance;
    }
}
