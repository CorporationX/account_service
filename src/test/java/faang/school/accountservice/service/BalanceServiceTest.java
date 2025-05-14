package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    @InjectMocks
    private BalanceService balanceService;

    private Long accountId = 1L;
    BalanceDto balanceDto;
    Balance savedBalance;
    BalanceAudit audit;

    Account account = new Account();

    @BeforeEach
    void setUp() {
        balanceDto = BalanceDto.builder()
                .accountId(accountId)
                .actualBalance(new BigDecimal("100"))
                .authorizedBalance(new BigDecimal("50"))
                .build();

         savedBalance = Balance.builder()
                .account(account)
                .authorizedBalance(BigDecimal.ZERO)
                .actualBalance(BigDecimal.ZERO)
                .version(0)
                .build();

         audit = BalanceAudit.builder()
                .account(account)
                .balanceVersion(savedBalance.getVersion())
                .authorizedBalance(savedBalance.getAuthorizedBalance())
                .actualBalance(savedBalance.getActualBalance())
                .operationId(null)
                .build();
    }

    @Nested
    class Create {
        @Test
        void success() {
            account.setId(accountId);

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(balanceRepository.save(any(Balance.class))).thenReturn(savedBalance);
            when(balanceAuditMapper.toAudit(any(Balance.class), nullable(Long.class))).thenReturn(audit);
            when(balanceMapper.toDto(savedBalance)).thenReturn(balanceDto);

            BalanceDto result = balanceService.create(accountId);

            verify(accountRepository).findById(accountId);
            verify(balanceRepository).save(any(Balance.class));
            verify(balanceAuditMapper).toAudit(any(Balance.class), nullable(Long.class));
            verify(balanceAuditRepository).save(any(BalanceAudit.class));
            verify(balanceMapper).toDto(savedBalance);

            assertEquals(balanceDto, result);
        }
    }

    @Nested
    class GetByAccountId {
        @Test
        void success() {
            Balance balance = new Balance();
            when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

            balanceService.getByAccountId(accountId);

            verify(balanceRepository, times(1)).findByAccountId(accountId);
            verify(balanceMapper, times(1)).toDto(any(Balance.class));
        }

        @Test
        void entityNotFoundException() {
            when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                balanceService.getByAccountId(accountId);
            });

            verify(balanceRepository, times(1)).findByAccountId(accountId);
            verify(balanceMapper, never()).toDto(any(Balance.class));
        }
    }

}