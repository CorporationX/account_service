package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Account account;
    private Balance balance;
    private BalanceDto balanceDto;
    private BigDecimal newCurrentBalance;
    private BigDecimal newAuthorizedBalance;

    @BeforeEach
    void setUp(){
        Long accountId = 1L;
        account = Account.builder()
                .id(accountId)
                .build();
        balance = Balance.builder()
                .id(1L)
                .account(account)
                .currentBalance(BigDecimal.ZERO)
                .authorizedBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();
        balanceDto = BalanceDto.builder()
                .id(balance.getId())
                .account(AccountDto.builder().id(account.getId()).build())
                .currentBalance(balance.getCurrentBalance())
                .authorizedBalance(balance.getAuthorizedBalance())
                .createdAt(balance.getCreatedAt())
                .build();
        newCurrentBalance = BigDecimal.TEN;
        newAuthorizedBalance = BigDecimal.valueOf(100);
    }

    @Test
    void createBalanceShouldCreateBalance(){
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);
        when(balanceAuditMapper.fromBalance(balance, null)).thenReturn(new BalanceAudit());
        BalanceDto result = balanceService.createBalance(account.getId());
        assertThat(result).isEqualTo(balanceDto);
        verify(balanceAuditRepository, times(1)).save(any(BalanceAudit.class));
    }

    @Test
    void createBalanceShouldThrowResourceNotFoundException(){
        when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> balanceService.createBalance(account.getId()));
    }

    @Test
    void getBalanceShouldReturnBalance(){
        when(balanceRepository.findByAccountId(account.getId())).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);
        BalanceDto result = balanceService.getBalance(account.getId());
        assertThat(result).isEqualTo(balanceDto);
    }

    @Test
    void getBalanceShouldThrowResourceNotFoundException(){
        when(balanceRepository.findByAccountId(account.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> balanceService.getBalance(account.getId()));
    }

    @Test
    void updateBalanceShouldUpdateBalance(){
        when(balanceRepository.findByAccountId(account.getId())).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);
        BalanceDto result = balanceService.updateBalance(account.getId(), newCurrentBalance, newAuthorizedBalance);
        assertThat(result).isEqualTo(balanceDto);
        assertThat(balance.getCurrentBalance()).isEqualTo(newCurrentBalance);
        assertThat(balance.getAuthorizedBalance()).isEqualTo(newAuthorizedBalance);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    void updateBalanceShouldThrowResourceNotFoundException(){
        when(balanceRepository.findByAccountId(account.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> balanceService
                .updateBalance(account.getId(), newCurrentBalance, newAuthorizedBalance));
    }
}