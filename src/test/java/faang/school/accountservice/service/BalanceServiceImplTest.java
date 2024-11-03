package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @InjectMocks
    private BalanceServiceImpl service;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private BalanceJpaRepository balanceJpaRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceAuditMapper auditMapper;

    @Mock
    private BalanceMapper mapper;

    @Captor
    private ArgumentCaptor<Balance> balanceCaptor;

    private BalanceDto balanceDto;
    private Balance balance;
    private Account account;
    private final long accountId = 1L;
    private final long balanceId = 2L;

    @BeforeEach
    public void init() {
        balance = new Balance();
        balance.setId(balanceId);

        balance.setVersion(1);

        balanceDto = new BalanceDto();
        balanceDto.setId(1L);
        balanceDto.setAccountId(1L);

        account = new Account();

        Mockito.lenient().when(mapper.toDto(balance, account))
                .thenReturn(balanceDto);
        Mockito.lenient().when(mapper.toEntity(balanceDto, account))
                .thenReturn(balance);
        Mockito.lenient().when(balanceJpaRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(balance));
        Mockito.lenient().when(accountRepository.getReferenceById(accountId)).thenReturn(account);
    }

    @Test
    void create_whenOk() {
        service.create(balanceDto);

        verify(balanceJpaRepository, times(1))
                .save(balance);
        verify(mapper, times(1)).toEntity(balanceDto, account);
        verify(balanceAuditRepository, times(1))
                .save(auditMapper.toEntity(balance));
    }

    @Test
    void update_whenOk() {
        final ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);

        service.update(balanceDto);

        verify(balanceJpaRepository, times(1))
                .save(captor.capture());
        verify(balanceAuditRepository, times(1))
                .save(auditMapper.toEntity(balance));

        Balance actual = captor.getValue();
        Assertions.assertNotNull(actual.getUpdatedAt());
        Assertions.assertNull(actual.getCreatedAt());
    }

    @Test
    void updateBalanceWithoutBalanceAudit_balanceExists_updatesBalance() {
        BigDecimal newBalance = BigDecimal.valueOf(200);
        when(balanceJpaRepository.findById(balanceId)).thenReturn(Optional.of(balance));

        service.updateBalanceWithoutBalanceAudit(balanceId, newBalance);

        verify(balanceJpaRepository, times(1)).save(balanceCaptor.capture());
        Balance balance = balanceCaptor.getValue();
        this.balance.setCurAuthBalance(newBalance);
        Assertions.assertEquals(this.balance, balance);
    }

    @Test
    void updateBalanceWithoutBalanceAudit_balanceDoesNotExist_throwsException() {
        String correctMessage = "Balance %d not found".formatted(balanceId);
        BigDecimal newBalance = BigDecimal.valueOf(200);
        when(balanceJpaRepository.findById(balanceId)).thenReturn(Optional.empty());


        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> service.updateBalanceWithoutBalanceAudit(balanceId, newBalance));

        verify(balanceJpaRepository, never()).save(any(Balance.class));
        Assertions.assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void getBalance_whenOk() {
        service.getBalance(accountId);

        verify(mapper, times(1)).toDto(balance, account);
        verify(balanceJpaRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getBalance_whenAccountNotExist() {
        Mockito.lenient().when(balanceJpaRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.getBalance(accountId));

        verify(mapper, never()).toDto(balance, account);
    }
}
