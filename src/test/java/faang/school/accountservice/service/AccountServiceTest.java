package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.Account;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Bank;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Spy
    private AccountMapperImpl accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void openAccount_ShouldCreateAndReturnAccountDto() {
        AccountCreateDto accountCreateDto = new AccountCreateDto();
        accountCreateDto.setBank(Bank.TINKOFF);
        Account account = new Account();
        when(accountRepository.save(any())).thenReturn(account);

        AccountDto accountDto = accountService.openAccount(accountCreateDto);

        assertNotNull(accountDto);
    }

    @Test
    public void getAccountById_ShouldReturnAccount() {
        long accountId = 1L;
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(accountId);

        assertNotNull(result);
    }

    @Test
    public void getAccountById_ShouldThrowExceptionWhenAccountNotFound() {
        long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(accountId));
    }

    @Test
    public void getAccountDtoById_ShouldReturnAccountDto() {
        long accountId = 1L;
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountDto accountDto = accountService.getAccountDtoById(accountId);

        assertNotNull(accountDto);
    }

    @Test
    public void getAccountByUserId_ShouldReturnAccountDtoList() {
        long userId = 1L;
        List<Account> accounts = List.of(new Account());
        when(accountRepository.findByUserId(userId)).thenReturn(accounts.stream());

        List<AccountDto> accountDtos = accountService.getAccountByUserId(userId);

        assertNotNull(accountDtos);
    }

    @Test
    public void getAccountByProjectId_ShouldReturnAccountDtoList() {
        long projectId = 1L;
        List<Account> accounts = List.of(new Account());
        when(accountRepository.findByProjectId(projectId)).thenReturn(accounts.stream());

        List<AccountDto> accountDtos = accountService.getAccountByProjectId(projectId);

        assertNotNull(accountDtos);
    }

    @Nested
    class retryableMethodsTests {
        @BeforeEach
        public void init() {
            RetryContext retryContext = mock(RetryContext.class);
            when(retryContext.getRetryCount()).thenReturn(-1);
            RetrySynchronizationManager.register(retryContext);
        }

        @Test
        public void friezeAccount_ShouldFriezeAccount() {
            long accountId = 1L;
            Account account = new Account();
            account.setStatus(AccountStatus.ACTIVE);
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            AccountDto accountDto = accountService.friezeAccount(accountId);

            assertEquals(AccountStatus.FROZEN, accountDto.getStatus());
        }

        @Test
        public void friezeAccount_ShouldThrowExceptionWhenAccountAlreadyFrozen() {
            long accountId = 1L;
            Account account = new Account();
            account.setStatus(AccountStatus.FROZEN);
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            assertThrows(IllegalStateException.class, () -> accountService.friezeAccount(accountId));
        }

        @Test
        public void closeAccount_ShouldCloseAccount() {
            long accountId = 1L;
            Account account = new Account();
            account.setStatus(AccountStatus.ACTIVE);
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            AccountDto accountDto = accountService.closeAccount(accountId);

            assertEquals(AccountStatus.CLOSED, accountDto.getStatus());
        }

        @Test
        public void closeAccount_ShouldThrowExceptionWhenAccountAlreadyClosed() {
            long accountId = 1L;
            Account account = new Account();
            account.setStatus(AccountStatus.CLOSED);
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            assertThrows(IllegalStateException.class, () -> accountService.closeAccount(accountId));
        }
    }
}
