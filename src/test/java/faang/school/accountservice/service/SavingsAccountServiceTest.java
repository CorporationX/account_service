package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.TariffAndRateMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createAccountDto;
import static faang.school.accountservice.util.TestDataFactory.createSavingsAccount;
import static faang.school.accountservice.util.TestDataFactory.createTariffAndRateDto;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {
    @InjectMocks
    private SavingsAccountService savingsAccountService;
    @Mock
    private SavingsAccountRepository savingsAccountRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TariffAndRateMapper tariffRateMapper;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private FreeAccountNumberService freeAccountNumberService;
    @Mock
    private SavingsAccountUpdateAsyncService savingsAccountAsyncService;

    @Test
    void getTariffAndRateByAccountId() {
        // given - precondition
        Long accountId = 1L;
        var savingsAccount = createSavingsAccount();
        var tariffAndRateDto = createTariffAndRateDto();

        when(savingsAccountRepository.findById(accountId)).thenReturn(of(savingsAccount));
        when(tariffRateMapper.mapToDto(savingsAccount)).thenReturn(tariffAndRateDto);

        // when - action
        var actualResult = savingsAccountService.getTariffAndRateByAccountId(accountId);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(tariffAndRateDto);
    }

    @Test
    void getAccountByClientId() {
        // given - precondition
        var savingsAccount = createSavingsAccount();
        var tariffAndRateDto = createTariffAndRateDto();

        when(savingsAccountRepository.findSavingsAccountByAccountNumber(ACCOUNT_NUMBER)).thenReturn(of(savingsAccount));
        when(tariffRateMapper.mapToDto(savingsAccount)).thenReturn(tariffAndRateDto);

        // when - action
        var actualResult = savingsAccountService.getAccountByClientId(ACCOUNT_NUMBER);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(tariffAndRateDto);
    }
    @Test
    void givenValidAccountWhenOpenAccountThenReturnSavedAccount() {
        // given - precondition
        var accountNumber = ACCOUNT_NUMBER;
        var accountDto = createAccountDto();
        var account = TestDataFactory.createAccount();
        var savingsAccount = createSavingsAccount();

        when(freeAccountNumberService.getNextSavingsAccountNumber()).thenReturn(accountNumber);
        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        when(accountMapper.toDto(account)).thenReturn(accountDto);

        // when - action
        var actualResult = savingsAccountService.openAccount(accountDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(accountDto);
    }
}