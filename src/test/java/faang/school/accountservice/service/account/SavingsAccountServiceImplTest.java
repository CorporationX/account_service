package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.mapper.SavingsAccountMapperImpl;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.SavingsAccount;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.AccountJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceImplTest {

    @Spy
    private SavingsAccountMapperImpl savingsAccountMapper;

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffHistoryRepository tariffHistoryRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private SavingsAccountServiceImpl savingsAccountService;

    @Captor
    ArgumentCaptor<SavingsAccount> savingsAccountArgumentCaptor;

    @Captor
    ArgumentCaptor<TariffHistory> tariffHistoryArgumentCaptor;

    @Captor
    ArgumentCaptor<BigDecimal> bigDecimalArgumentCaptor;

    @Test
    @DisplayName("Successfully open savings account with valid tariff and account")
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
        when(accountJpaRepository.findById(dto.getAccountId())).thenReturn(Optional.of(account));
        when(savingsAccountRepository.save(savingsAccountArgumentCaptor.capture()))
                .thenReturn(SavingsAccount.builder().account(account).build());
        SavingsAccountDto resultDto = savingsAccountService.openSavingsAccount(dto);
        verify(tariffRepository, times(1)).findById(dto.getTariffId());
        verify(accountJpaRepository, times(1)).findById(dto.getAccountId());
        verify(savingsAccountRepository, times(1)).save(savingsAccountArgumentCaptor.capture());
        verify(tariffHistoryRepository, times(1)).save(tariffHistoryArgumentCaptor.capture());
        assertEquals(dto.getAccountId(), resultDto.getAccountId());
    }

    @Test
    @DisplayName("Throw EntityNotFoundException when opening savings account with invalid data")
    public void testOpenSavingsAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> savingsAccountService.openSavingsAccount(new SavingsAccountDto()));
    }

    @Test
    @DisplayName("Successfully retrieve savings account by ID")
    public void testGetSavingsAccount() {
        Long savingsAccountId = 1L;
        when(savingsAccountRepository.findSavingsAccountWithDetails(savingsAccountId)).thenReturn(Optional.of(new SavingsAccountDto()));
        savingsAccountService.getSavingsAccount(savingsAccountId);
        verify(savingsAccountRepository, times(1)).findSavingsAccountWithDetails(savingsAccountId);
    }

    @Test
    @DisplayName("Throw EntityNotFoundException when retrieving non-existent savings account")
    public void testGetSavingsAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> savingsAccountService.getSavingsAccount(1L));
    }

    @Test
    @DisplayName("Successfully retrieve all savings accounts for a user")
    public void testGetSavingsAccountByUserId_Success() {
        Long userId = 1L;
        BigDecimal rate = BigDecimal.valueOf(5.5);
        List<String> numbers = List.of("429346812734628", "38642897364528736");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        List<Object[]> savingsAccounts = new ArrayList<>();
        savingsAccounts.add(new Object[]{userId, 1L, rate, timestamp, timestamp, timestamp});
        savingsAccounts.add(new Object[]{userId, 2L, rate, timestamp, timestamp, timestamp});
        when(accountJpaRepository.findNumbersByUserId(userId)).thenReturn(numbers);
        when(savingsAccountRepository.getSavingsAccountsWithLastTariffRate(numbers)).thenReturn(savingsAccounts);
        List<SavingsAccountDto> results = savingsAccountService.getSavingsAccountByUserId(userId);
        verify(accountJpaRepository, times(1)).findNumbersByUserId(userId);
        verify(savingsAccountRepository, times(1)).getSavingsAccountsWithLastTariffRate(numbers);
        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(2, results.size()),
                () -> assertEquals(userId, results.get(0).getId()),
                () -> assertEquals(userId, results.get(1).getId())
        );
    }

    @Test
    @DisplayName("Throw EntityNotFoundException when retrieving savings accounts for non-existent user")
    public void testGetSavingsAccountByUserIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> savingsAccountService.getSavingsAccount(1L));
    }

    @Test
    @DisplayName("Successfully calculate percentage for savings account")
    public void calculatePercentSuccess() {
        BigDecimal initValue = BigDecimal.valueOf(100_000);
        Long balanceId = 1L;
        BigDecimal rate = BigDecimal.valueOf(5.5);
        Long savingsAccountId = 2L;
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);
        Balance balance = mock(Balance.class);
        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
        when(balance.getActualValue()).thenReturn(initValue);
        savingsAccountService.calculatePercent(balanceId, rate, savingsAccountId);
        verify(savingsAccountRepository, times(1)).findById(savingsAccountId);
        verify(balanceRepository, times(1)).findById(balanceId);
        verify(balance, times(1)).setActualValue(bigDecimalArgumentCaptor.capture());
        BigDecimal newBalance = bigDecimalArgumentCaptor.getValue();
        assertEquals(1, newBalance.compareTo(initValue));
    }
}