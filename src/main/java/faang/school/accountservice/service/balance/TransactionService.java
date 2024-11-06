package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.service.balance.transactionHandler.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final Map<TransactionType, TransactionHandler> handlers;

    @Autowired
    public TransactionService(List<TransactionHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(TransactionHandler::getType, handler -> handler));
    }

    public Balance processTransaction(TransactionDto transaction, Account account) {
        TransactionHandler handler = handlers.get(transaction.getTransactionType());
        if (handler == null) {
            throw new IllegalArgumentException("Invalid transaction type");
        }
        return handler.handle(transaction, account);
    }
}
