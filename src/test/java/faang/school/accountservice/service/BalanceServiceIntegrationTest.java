package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BalanceServiceIntegrationTest {

    @MockBean
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceService balanceService;

    private Balance balance;

    private Long balanceId;

    @BeforeEach
    void setUp() {
        String accountNumber = "ACC0123456789";
        Account account = Account.builder()
                .id(1L)
                .accountNumber(accountNumber)
                .status(AccountStatus.ACTIVE)
                .ownerId(1L)
                .build();
        balanceId = 1L;
        balance = Balance.builder()
                .id(1L)
                .account(account)
                .authorizedBalance(new BigDecimal("0.00"))
                .actualBalance(new BigDecimal("0.00"))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Retryable deposit authorized balance test: OptimisticLockException")
    void testDepositAuthorized_Success() {
        when(balanceRepository.getReferenceById(balanceId))
                .thenThrow(OptimisticLockException.class)
                .thenThrow(OptimisticLockException.class)
                .thenReturn(balance);

        BalanceDto result = balanceService.depositAuthorized(balanceId, new BigDecimal("100.00"));

        verify(balanceRepository, times(3)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("100.00"), result.authorizedBalance());
    }

    @Test
    @DisplayName("Retryable deposit actual balance test: OptimisticLockException")
    void testDepositActual_Success() {
        when(balanceRepository.getReferenceById(balanceId))
                .thenThrow(OptimisticLockException.class)
                .thenThrow(OptimisticLockException.class)
                .thenReturn(balance);

        BalanceDto result = balanceService.depositActual(balanceId, new BigDecimal("100.00"));

        verify(balanceRepository, times(3)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("100.00"), result.actualBalance());
    }


    @Test
    @DisplayName("Retryable withdraw authorized balance test: OptimisticLockException")
    void testWithdrawAuthorized_Success() {
        balance.setAuthorizedBalance(new BigDecimal("100.00"));
        when(balanceRepository.getReferenceById(balanceId))
                .thenThrow(OptimisticLockException.class)
                .thenThrow(OptimisticLockException.class)
                .thenReturn(balance);

        BalanceDto result = balanceService.withdrawAuthorized(balanceId, new BigDecimal("99.01"));

        verify(balanceRepository, times(3)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("0.99"), result.authorizedBalance());
    }

    @Test
    @DisplayName("Retryable withdraw actual balance test: OptimisticLockException")
    void testWithdrawActual_Retryable() {
        balance.setActualBalance(new BigDecimal("100.00"));
        when(balanceRepository.getReferenceById(balanceId))
                .thenThrow(OptimisticLockException.class)
                .thenThrow(OptimisticLockException.class)
                .thenReturn(balance);

        BalanceDto result = balanceService.withdrawActual(balanceId, new BigDecimal("99.01"));

        verify(balanceRepository, times(3)).getReferenceById(balanceId);
        assertEquals(new BigDecimal("0.99"), result.actualBalance());
    }
}
