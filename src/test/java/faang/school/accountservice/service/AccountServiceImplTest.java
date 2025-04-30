package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Spy
    private AccountMapperImpl accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Long accountId;
    private String generatedAccountNumber;
    private AccountDto inputDto;
    private Account account;

    @BeforeEach
    void setUp() {
        accountId = 1L;
        generatedAccountNumber = "20250400000001";

        inputDto = AccountDto.builder()
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL)
                .currency(Currency.RUB)
                .build();

        account = Account.builder()
                .id(accountId)
                .accountNumber(generatedAccountNumber)
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL)
                .currency(Currency.RUB)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .version(1L)
                .build();
    }

    @Test
    void shouldOpenAccountSuccessfully() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(generatedAccountNumber);
        when(accountRepository.save(any(Account.class))).thenReturn(account);


        AccountDto result = accountService.openAccount(inputDto);

        assertNotNull(result);
        assertEquals(generatedAccountNumber, result.getAccountNumber());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
    }

    @Test
    void shouldGetAccountSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccount(accountId);

        assertNotNull(result);
        assertEquals(account.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(accountId));
    }

    @Test
    void shouldBlockAccountSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account account = invocation.getArgument(0);
                    account.setStatus(AccountStatus.FROZEN);
                    return account;
                });

        AccountDto result = accountService.blockAccount(accountId);

        assertEquals(AccountStatus.FROZEN, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenBlockingClosedAccount() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> accountService.blockAccount(accountId));

        assertTrue(ex.getMessage().contains("Cannot block a closed account"));
    }

    @Test
    void shouldCloseAccountSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account account = invocation.getArgument(0);
                    account.setStatus(AccountStatus.CLOSED);
                    account.setClosedAt(LocalDateTime.now());
                    return account;
                });

        AccountDto result = accountService.closeAccount(accountId);

        assertEquals(AccountStatus.CLOSED, result.getStatus());
        assertNotNull(result.getClosedAt());
    }

    @Test
    void shouldThrowExceptionWhenClosingAlreadyClosedAccount() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> accountService.closeAccount(accountId));

        assertTrue(ex.getMessage().contains("Account is already closed"));
    }

    @Test
    void shouldThrowOptimisticLockExceptionWhenBlockingAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenThrow(JpaOptimisticLockingFailureException.class);

        assertThrows(JpaOptimisticLockingFailureException.class, () -> accountService.blockAccount(accountId));
    }

    @Test
    void shouldThrowOptimisticLockExceptionWhenClosingAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenThrow(JpaOptimisticLockingFailureException.class);

        assertThrows(JpaOptimisticLockingFailureException.class, () -> accountService.closeAccount(accountId));
    }
}
