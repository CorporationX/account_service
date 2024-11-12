package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.service.balance.transactionHandler.TransactionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private TransactionService transactionService;
    private Balance balance;
    private TransactionDto transactionDto;

    @Mock
    private TransactionHandler mockHandler;

    @Mock
    Account account;

    @BeforeEach
    void setUp() {
        when(mockHandler.getType()).thenReturn(TransactionType.RETENTION);

        List<TransactionHandler> handlers = Collections.singletonList(mockHandler);
        transactionService = new TransactionService(handlers);

        balance = Balance.builder()
                .authorizationBalance(BigDecimal.valueOf(100.00))
                .actualBalance(BigDecimal.valueOf(150.00))
                .paymentNumber(12345L)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 5, 12, 0))
                .build();

        transactionDto = TransactionDto.builder()
                .transactionType(TransactionType.RETENTION)
                .build();

    }

    @Test
    void processTransaction_validType_invokesHandler() {
        when(mockHandler.handle(transactionDto, account)).thenReturn(balance);

        Balance result = transactionService.processTransaction(transactionDto, account);

        assertEquals(balance, result);
        verify(mockHandler, times(1)).handle(transactionDto, account);
    }

    @Test
    void processTransaction_invalidType_throwsIllegalArgumentException() {
        transactionDto = new TransactionDto();

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.processTransaction(transactionDto, account);
        });
    }
}
