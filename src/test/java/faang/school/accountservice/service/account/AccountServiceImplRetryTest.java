package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataConflictRetryException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        "spring.retry.account-update.max-attempts=3",
        "spring.retry.account-update.delay-ms=10",
        "spring.retry.account-update.multiplier=1.0"
})
@SpringBootTest
class AccountServiceImplRetryTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private AccountValidator accountValidator;

    @SpyBean
    private AccountMapper accountMapper;

    @Test
    void updateAccountStatusExhaustsRetriesThenCallsRecover() {
        Long id = 42L;
        Account existing = Account.builder().id(id).status(AccountStatus.ACTIVE).build();

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.saveAndFlush(any(Account.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Account.class, id));

        assertThrows(DataConflictRetryException.class,
                () -> accountService.updateAccountStatus(id, AccountStatus.FROZEN));

        verify(accountRepository, times(3)).saveAndFlush(any(Account.class));
        verify(accountMapper, times(0)).toAccountDto(any(Account.class));
    }

    @Test
    void updateAccountStatusTransientConflictThenSucceedsOnRetry() {
        Long id = 7L;
        Account existing = Account.builder().id(id).status(AccountStatus.ACTIVE).build();

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.saveAndFlush(any(Account.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Account.class, id))
                .thenAnswer(inv -> inv.getArgument(0));

        accountService.updateAccountStatus(id, AccountStatus.FROZEN);

        verify(accountRepository, times(2)).saveAndFlush(any(Account.class));
        verify(accountMapper, times(1)).toAccountDto(any(Account.class));
    }
}

