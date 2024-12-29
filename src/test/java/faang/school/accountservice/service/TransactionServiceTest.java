package faang.school.accountservice.service;

import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    Long transactionId;
    BigDecimal amount;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionId = 1L;
        amount = BigDecimal.valueOf(10);
        transaction = Transaction.builder()
                .id(transactionId)
                .transactionAmount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Get transaction success: valid input")
    void testGetTransactionById_Success() {

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(transactionId);

        assertEquals(transaction, result);
    }

    @Test
    @DisplayName("Get transaction fail: transaction not found")
    void testGetTransactionById_TransactionNotFound_Fail() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(transactionId));

        assertEquals(String.format("Transaction with id %d doesn't exist", transactionId), ex.getMessage());
    }

    @Test
    @DisplayName("Approve transaction success: valid input")
    void testApproveTransaction_Success() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.approveTransaction(transactionId);

        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(transaction);

        assertEquals(TransactionStatus.APPROVED, transaction.getTransactionStatus());
    }

    @Test
    @DisplayName("Reject transaction success: valid input")
    void testRejectTransaction_Success() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.rejectTransaction(transactionId);

        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(transaction);

        assertEquals(TransactionStatus.REJECTED, transaction.getTransactionStatus());
    }

    @Test
    @DisplayName("Validate transaction exists success: valid input")
    void testValidateTransactionExists_Success() {
        BigDecimal amount = BigDecimal.valueOf(10);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.validateTransactionExists(transactionId, amount);

        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    @DisplayName("Validate transaction exists fail: invalid amount")
    void testValidateTransactionExists_InvalidAmount_Fail() {
        BigDecimal amount = BigDecimal.valueOf(20);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThrows(EntityNotFoundException.class, () -> transactionService.validateTransactionExists(transactionId, amount));
    }
}