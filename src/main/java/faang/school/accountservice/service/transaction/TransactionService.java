package faang.school.accountservice.service.transaction;

import faang.school.accountservice.dto.transaction.TransactionDto;
import faang.school.accountservice.dto.transaction.TransactionDtoToCreate;

public interface TransactionService {

    TransactionDto createTransaction(Long userId, TransactionDtoToCreate dto);

    TransactionDto getTransaction(Long id);

    TransactionDto cancelTransaction(Long userid, Long transactionId);

}
