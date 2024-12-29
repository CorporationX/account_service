package faang.school.accountservice.service;

import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Transaction with id %d doesn't exist", transactionId))
        );
    }

    @Transactional
    public void approveTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setTransactionStatus(TransactionStatus.APPROVED);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void rejectTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setTransactionStatus(TransactionStatus.REJECTED);
        transactionRepository.save(transaction);
    }

    public void validateTransactionExists(Long transactionId, BigDecimal amount) {
        Transaction transaction = getTransactionById(transactionId);
        if (amount.compareTo(transaction.getTransactionAmount()) != 0) {
            throw new EntityNotFoundException(String.format("Transaction with id %d doesn't exist", transactionId));
        }
    }
}
