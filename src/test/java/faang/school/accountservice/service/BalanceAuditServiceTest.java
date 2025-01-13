package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.repository.BalanceAuditRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class BalanceAuditServiceTest {
    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @InjectMocks
    private BalanceAuditService balanceAuditService;

    @Test
    @DisplayName("Create balance audit success")
    void testCreateBalanceAuditSuccess() {
        Account mockAccount = new Account();
        Balance mockBalance = new Balance();
        mockBalance.setAuthorizedBalance(new BigDecimal("1000.00"));
        mockBalance.setActualBalance(new BigDecimal("950.00"));
        mockAccount.setBalance(mockBalance);

        Transaction mockTransaction = new Transaction();
        mockTransaction.setTransactionAmount(new BigDecimal("100.00"));
        mockTransaction.setCreatedAt(LocalDateTime.now());

        BalanceAudit expectedBalanceAudit = BalanceAudit.builder()
                .account(mockAccount)
                .balanceVersion(mockBalance.getBalanceVersion()) // Assuming it's set correctly
                .authorizedBalance(mockBalance.getAuthorizedBalance())
                .operationAmount(mockTransaction.getTransactionAmount())
                .transaction(mockTransaction)
                .createdAt(mockTransaction.getCreatedAt())
                .build();

        balanceAuditService.createAuditEntry(mockAccount, mockTransaction);

        ArgumentCaptor<BalanceAudit> argumentCaptor = ArgumentCaptor.forClass(BalanceAudit.class);
        verify(balanceAuditRepository, times(1)).save(argumentCaptor.capture());

        BalanceAudit capturedBalanceAudit = argumentCaptor.getValue();
        assertEquals(expectedBalanceAudit.getAuthorizedBalance(), capturedBalanceAudit.getAuthorizedBalance());
        assertEquals(expectedBalanceAudit.getOperationAmount(), capturedBalanceAudit.getOperationAmount());
        assertEquals(expectedBalanceAudit.getCreatedAt(), capturedBalanceAudit.getCreatedAt());
        assertEquals(expectedBalanceAudit.getAccount(), capturedBalanceAudit.getAccount());
        assertEquals(expectedBalanceAudit.getTransaction(), capturedBalanceAudit.getTransaction());
    }

    @Test
    @DisplayName("Create balance audit with null account")
    void testCreateBalanceAuditWithNullAccount() {
        Transaction mockTransaction = new Transaction();

        assertThrows(NullPointerException.class, () -> {
            balanceAuditService.createAuditEntry(null, mockTransaction);
        });

        verifyNoInteractions(balanceAuditRepository);
    }

    @Test
    @DisplayName("Create balance audit with null transaction")
    void testCreateBalanceAuditWithNullTransaction() {
        Account mockAccount = new Account();
        Balance mockBalance = new Balance();
        mockBalance.setAuthorizedBalance(new BigDecimal("1000.00"));
        mockBalance.setActualBalance(new BigDecimal("950.00"));
        mockAccount.setBalance(mockBalance);

        assertThrows(NullPointerException.class, () -> {
            balanceAuditService.createAuditEntry(mockAccount, null);
        });

        verifyNoInteractions(balanceAuditRepository);
    }
}
