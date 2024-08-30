package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createAccount;
import static faang.school.accountservice.util.TestDataFactory.createAccountDto;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;

    @Test
    void givenAccountIdWhenGetAccountByIdThenReturnAccount() {
        // given - precondition
        var expectedResult = createAccountDto();
        var account = createAccount();

        when(accountRepository.findAccountByNumber(ACCOUNT_NUMBER)).thenReturn(of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedResult);

        // when - action
        var actualResult = accountService.getAccountByNumber(ACCOUNT_NUMBER);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void givenAccountWhenOpenAccountThenReturnAccount() {
        // given - precondition
        var expectedResult = createAccountDto();
        var account = createAccount();

        when(accountMapper.toDto(account)).thenReturn(expectedResult);
        when(accountMapper.toEntity(expectedResult)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // when - action
        var actualResult = accountService.openAccount(expectedResult);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void givenAccountWhenBlockAccountThenAccountIsBlocked() {
        // given - precondition
        var account = createAccount();

        when(accountRepository.findAccountByNumber(ACCOUNT_NUMBER)).thenReturn(of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // when - action
        accountService.blockAccount(ACCOUNT_NUMBER);

        // then - verify the output
        verify(accountRepository, times(1)).findAccountByNumber(ACCOUNT_NUMBER);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void givenAccountWhenCloseAccountThenAccountIsClosed() {
        // given - precondition
        var account = createAccount();

        when(accountRepository.findAccountByNumber(ACCOUNT_NUMBER)).thenReturn(of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // when - action
        accountService.closeAccount(ACCOUNT_NUMBER);

        // then - verify the output
        verify(accountRepository, times(1)).findAccountByNumber(ACCOUNT_NUMBER);
        verify(accountRepository, times(1)).save(account);
    }
}