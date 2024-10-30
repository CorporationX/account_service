package faang.school.accountservice.service;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.savings.SavingsAccount;
import faang.school.accountservice.model.savings.Tariff;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountServiceTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private TariffHistoryRepository tariffHistoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TariffService tariffService;

    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    private UUID accountId;
    private UUID tariffId;
    private UUID ownerId;
    private Account account;
    private Tariff tariff;
    private SavingsAccount savingsAccount;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        tariffId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        account = Account.builder()
                .id(accountId)
                .build();

        tariff = Tariff.builder()
                .id(tariffId)
                .build();

        savingsAccount = SavingsAccount.builder()
                .id(accountId)
                .account(account)
                .lastCalculatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testOpenSavingsAccountWhenAccountExists() {
        when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.of(savingsAccount));

        SavingsAccount existingAccount = savingsAccountService.openSavingsAccount(accountId, tariffId);

        assertNotNull(existingAccount);
        assertEquals(savingsAccount, existingAccount);
        verify(savingsAccountRepository, never()).save(any(SavingsAccount.class));
    }

    @Test
    void testGetSavingsAccountByIdFound() {
        when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountMapper.toSavingsAccountDto(savingsAccount)).thenReturn(new SavingsAccountDto());

        SavingsAccountDto dto = savingsAccountService.getSavingsAccountById(accountId);

        assertNotNull(dto);
        verify(savingsAccountRepository).findById(accountId);
    }

    @Test
    void testGetSavingsAccountByIdNotFound() {
        when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> savingsAccountService.getSavingsAccountById(accountId));
    }

    @Test
    void testGetSavingsAccountByOwnerIdFound() {
        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(account));
        when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountMapper.toSavingsAccountDto(savingsAccount)).thenReturn(new SavingsAccountDto());

        SavingsAccountDto dto = savingsAccountService.getSavingsAccountByOwnerId(ownerId);

        assertNotNull(dto);
        verify(accountRepository).findByOwnerId(ownerId);
        verify(savingsAccountRepository).findById(accountId);
    }

    @Test
    void testGetSavingsAccountByOwnerIdAccountNotFound() {
        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> savingsAccountService.getSavingsAccountByOwnerId(ownerId));
    }

    @Test
    void testGetSavingsAccountByOwnerIdSavingsAccountNotFound() {
        when(accountRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(account));
        when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> savingsAccountService.getSavingsAccountByOwnerId(ownerId));
    }
}
