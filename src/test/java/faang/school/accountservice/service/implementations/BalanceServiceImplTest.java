package faang.school.accountservice.service.implementations;
import faang.school.accountservice.dto.BalanceOperationDto;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.strategy.BalanceOperationStrategy;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceOperationStrategy authorizeStrategy;

    @Mock
    private BalanceOperationStrategy depositStrategy;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Balance balance;
    private Account account;

    @BeforeEach
    void setUp() {
        balanceService = new BalanceServiceImpl(
                balanceRepository,
                balanceMapper,
                List.of(authorizeStrategy, depositStrategy),
                accountRepository
        );

        account = Account.builder()
                .id(1L)
                .accountNumber("123")
                .build();

        balance = Balance.builder()
                .id(1L)
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authorizedBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateBalanceWhenSuccessful() {
        when(accountRepository.findByIdWithBalance(1L)).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        when(balanceMapper.toBalanceResponseDto(any())).thenReturn(new BalanceResponseDto());

        BalanceResponseDto response = balanceService.createBalance(1L);

        assertNotNull(response);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void testCreateBalanceWhenBalanceExists() {
        account.setBalance(balance);
        when(accountRepository.findByIdWithBalance(1L)).thenReturn(Optional.of(account));

        assertThrows(EntityExistsException.class, () -> balanceService.createBalance(1L));
        verify(balanceRepository, never()).save(any());
    }

    @Test
    void testUpdateBalanceWhenSuccessful() {
        BalanceOperationDto dto = new BalanceOperationDto();
        dto.setAmount(BigDecimal.TEN);
        dto.setOperationType(OperationType.AUTHORIZE);

        when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.of(balance));
        when(authorizeStrategy.isApplicable(OperationType.AUTHORIZE)).thenReturn(true);
        doNothing().when(authorizeStrategy).apply(balance, BigDecimal.TEN);
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toBalanceResponseDto(balance)).thenReturn(new BalanceResponseDto());

        BalanceResponseDto response = balanceService.updateBalance(1L, dto);

        assertNotNull(response);
        verify(authorizeStrategy).apply(balance, BigDecimal.TEN);
        verify(balanceRepository).save(balance);
    }

    @Test
    void testGetBalanceWhenExists() {
        when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.of(balance));
        when(balanceMapper.toBalanceResponseDto(balance)).thenReturn(new BalanceResponseDto());

        BalanceResponseDto result = balanceService.getBalance(1L);

        assertNotNull(result);
        verify(balanceRepository).findByAccountId(1L);
    }

    @Test
    void testGetBalanceWhenNotFound() {
        when(balanceRepository.findByAccountId(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(2L));
    }
}