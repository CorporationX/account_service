package faang.school.accountservice.service.implementations;

import faang.school.accountservice.dto.BalanceOperationDto;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Test
    void testCreateBalanceWhenSuccessful() {
        Account account = new Account();
        account.setId(1L);

        Mockito.when(accountRepository.findByIdWithBalance(1L)).thenReturn(Optional.of(account));
        Mockito.when(balanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BalanceResponseDto dto = new BalanceResponseDto();
        Mockito.when(balanceMapper.toBalanceResponseDto(any())).thenReturn(dto);

        BalanceResponseDto result = balanceService.createBalance(1L);

        assertNotNull(result);
        verify(balanceRepository).save(any());
    }

    @Test
    void testCreateBalanceWhenBalanceExists() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new Balance());

        Mockito.when(accountRepository.findByIdWithBalance(1L)).thenReturn(Optional.of(account));

        assertThrows(EntityExistsException.class, () -> balanceService.createBalance(1L));
    }

    @Test
    void testUpdateBalanceWhenSuccessful() {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.ZERO);
        balance.setAuthorizedBalance(BigDecimal.ZERO);

        Mockito.when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.of(balance));
        Mockito.when(balanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Mockito.when(balanceMapper.toBalanceResponseDto(any())).thenReturn(new BalanceResponseDto());

        BalanceOperationDto dto = new BalanceOperationDto();
        dto.setAccountId(1L);
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setOperationType(OperationType.DEPOSIT);

        BalanceResponseDto response = balanceService.updateBalance(dto);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100), balance.getActualBalance());
    }

    @Test
    void testGetBalanceWhenExists() {
        Balance balance = new Balance();
        balance.setId(10L);

        Mockito.when(balanceRepository.findByAccountId(1L)).thenReturn(Optional.of(balance));
        Mockito.when(balanceMapper.toBalanceResponseDto(balance)).thenReturn(new BalanceResponseDto());

        BalanceResponseDto dto = balanceService.getBalance(1L);

        assertNotNull(dto);
    }
}