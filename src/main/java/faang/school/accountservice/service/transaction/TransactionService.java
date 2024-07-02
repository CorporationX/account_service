package faang.school.accountservice.service.transaction;

import faang.school.accountservice.dto.transaction.TransactionDto;
import faang.school.accountservice.dto.transaction.TransactionDtoToCreate;

public interface TransactionService {
    
    void createTransaction(Long userId, TransactionDtoToCreate dto);

    TransactionDto getTransaction(long id);

    void cancelTransaction(long id);
}
