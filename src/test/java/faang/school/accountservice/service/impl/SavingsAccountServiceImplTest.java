package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.SavingsAccountMapperImpl;
import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.SavingsAccount;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.model.entity.TariffHistory;
import faang.school.accountservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceImplTest {

    @Spy
    private SavingsAccountMapperImpl savingsAccountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffHistoryRepository tariffHistoryRepository;

    @Mock
    private SavingsAccountRateRepository savingsAccountRateRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private SavingsAccountServiceImpl savingsAccountService;

    @Captor
    ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor;

    @Captor
    ArgumentCaptor<TariffHistory> tariffHistoryArgumentCaptor;

    @Test
    public void testOpenSavingsAccount() {
        Long tariffId = 1L;
        Long accountId = 2L;
        SavingsAccountDto dto = new SavingsAccountDto();
        dto.setTariffId(tariffId);
        dto.setAccountId(accountId);
        Tariff tariff = Tariff.builder()
                .id(tariffId)
                .name("tariff1").build();
        Account account = Account.builder()
                .id(dto.getAccountId()).build();
        when(tariffRepository.findById(dto.getTariffId())).thenReturn(Optional.of(tariff));
        when(accountRepository.findById(dto.getAccountId())).thenReturn(Optional.of(account));
        when(savingsAccountRepository.save(savingsAccountArgumentCaptor.capture()))
                .thenReturn(SavingsAccount.builder().account(account).build());

        SavingsAccountDto resultDto = savingsAccountService.openSavingsAccount(dto);

        verify(tariffRepository, times(1)).findById(dto.getTariffId());
        verify(accountRepository, times(1)).findById(dto.getAccountId());
        verify(savingsAccountRepository, times(1)).save(savingsAccountArgumentCaptor.capture());
        verify(tariffHistoryRepository, times(1)).save(tariffHistoryArgumentCaptor.capture());
        assertEquals(dto.getAccountId(), resultDto.getAccountId());
    }

    @Test
    public void testOpenSavingsAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> savingsAccountService.openSavingsAccount(new SavingsAccountDto()));
    }

    @Test
    public void testGetSavingsAccount() {
        Long savingsAccountId = 1L;
        when(savingsAccountRepository.findSavingsAccountWithDetails(savingsAccountId)).thenReturn(new SavingsAccountDto());

        savingsAccountService.getSavingsAccount(savingsAccountId);

        verify(savingsAccountRepository, times(1)).findSavingsAccountWithDetails(savingsAccountId);
    }

    @Test
    public void testGetSavingsAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> savingsAccountService.getSavingsAccount(1L));
    }

    @Test
    public void testGetSavingsAccountByUserId() {
        Long userId = 1L;
        Long savingsAccount1Id = 1L;
        Long savingsAccount2Id = 2L;
        List<String> numbers = List.of("429346812734628", "38642897364528736");
        SavingsAccount savingsAccount1 = SavingsAccount.builder().id(savingsAccount1Id).build();
        SavingsAccount savingsAccount2 = SavingsAccount.builder().id(savingsAccount2Id).build();
        List<SavingsAccount> savingsAccounts = List.of(savingsAccount1, savingsAccount2);

        when(accountRepository.findNumbersByUserId(userId)).thenReturn(numbers);
        when(savingsAccountRepository.findSaByAccountNumbers(numbers)).thenReturn(savingsAccounts);
        when(tariffHistoryRepository.findLatestTariffIdBySavingsAccountId(1L)).thenReturn(Optional.of(savingsAccount1Id));
        when(tariffHistoryRepository.findLatestTariffIdBySavingsAccountId(2L)).thenReturn(Optional.of(savingsAccount2Id));
        when(savingsAccountRateRepository.findLatestRateIdByTariffId(anyLong())).thenReturn(Optional.of(5.0));

        List<SavingsAccountDto> result = savingsAccountService.getSavingsAccountByUserId(userId);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(accountRepository).findNumbersByUserId(userId);
        verify(savingsAccountRepository).findSaByAccountNumbers(numbers);
        verify(savingsAccountMapper).toDtos(savingsAccounts);
        verify(tariffHistoryRepository, times(1)).findLatestTariffIdBySavingsAccountId(savingsAccount1Id);
        verify(tariffHistoryRepository, times(1)).findLatestTariffIdBySavingsAccountId(savingsAccount2Id);
    }

    @Test
    public void testGetSavingsAccountByUserIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> savingsAccountService.getSavingsAccount(1L));
    }

    @Test
    public void calculatePercentSuccess() {
        // TODO make tests
        Long balanceId = 1L;
        BigDecimal rate = BigDecimal.valueOf(5.5);
        Long savingsAccountId = 2L;
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        Balance balance = new Balance();
        balance.setId(balanceId);
        balance.setActualBalance(BigDecimal.valueOf(100_000));
        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
    }
}