package faang.school.savingsAccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.accountservice.dto.savingsAccount.BalanceDto;
import faang.school.accountservice.dto.savingsAccount.SavingsAccountRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.SavingsAccountService;
import faang.school.accountservice.service.TariffService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TariffService tariffService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    private final UUID testId = UUID.randomUUID();
    private final Long testAccountId = 1L;
    private final TariffType testTariffName = TariffType.BASE;
    private final LocalDate testDate = LocalDate.now();

    @Test
    void openSavingsAccount_WithValidData_ShouldReturnCreatedAccount() {
        SavingsAccountRequestDto request = new SavingsAccountRequestDto(testAccountId, testTariffName);
        Account mockAccount = new Account();
        Tariff mockTariff = Tariff.builder().rate(5.0).build();
        SavingsAccount expectedAccount = SavingsAccount.builder()
            .account(mockAccount)
            .tariff(mockTariff)
            .build();

        when(accountService.getAccountById(testAccountId)).thenReturn(mockAccount);
        when(tariffService.getTariff(testTariffName)).thenReturn(mockTariff);
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(expectedAccount);

        SavingsAccount result = savingsAccountService.openSavingsAccount(request);

        assertThat(result).isEqualTo(expectedAccount);
        assertThat(result.getTariffHistory()).isEmpty();
        verify(savingsAccountRepository).save(any(SavingsAccount.class));
    }


    @Test
    void getById_WhenExists_ShouldReturnAccount() {
        SavingsAccount mockAccount = buildMockSavingsAccount();
        when(savingsAccountRepository.findById(testId)).thenReturn(Optional.of(mockAccount));

        SavingsAccount result = savingsAccountService.getById(testId);

        assertThat(result).isEqualTo(mockAccount);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(savingsAccountRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savingsAccountService.getById(testId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining(testId.toString());
    }

    @Test
    void deposit_ShouldIncreaseBalance() {
        BalanceDto request = new BalanceDto(testAccountId, BigDecimal.valueOf(100));
        SavingsAccount mockAccount = buildMockSavingsAccountWithBalance(BigDecimal.valueOf(500));

        when(savingsAccountRepository.findByAccountId(testAccountId)).thenReturn(
            Optional.of(mockAccount));
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenAnswer(
            inv -> inv.getArgument(0));

        SavingsAccount result = savingsAccountService.deposit(request);

        assertThat(result.getBalance()).isEqualByComparingTo("600");
        verify(savingsAccountRepository).save(mockAccount);
    }

    @Test
    void withdraw_WhenInsufficientBalance_ShouldThrowException() {
        BalanceDto request = new BalanceDto(testAccountId, BigDecimal.valueOf(600));
        SavingsAccount mockAccount = buildMockSavingsAccountWithBalance(BigDecimal.valueOf(500));

        when(savingsAccountRepository.findByAccountId(testAccountId)).thenReturn(
            Optional.of(mockAccount));

        assertThatThrownBy(() -> savingsAccountService.withdraw(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Not enough funds");
    }

    @Test
    void calculateAndApplyInterest_ShouldCorrectlyUpdateBalance() {
        Tariff tariff = Tariff.builder().rate(10.0).build();
        SavingsAccount account = buildMockSavingsAccountWithTariff(tariff);
        account.setBalance(BigDecimal.valueOf(1000));

        savingsAccountService.calculateAndApplyInterest(account, testDate);

        assertThat(account.getBalance()).isEqualByComparingTo("1100");
        assertThat(account.getLastInterestDate()).isEqualTo(testDate);
        verify(savingsAccountRepository).save(account);
    }

    private SavingsAccount buildMockSavingsAccount() {
        return SavingsAccount.builder()
            .id(testId)
            .account(new Account())
            .tariff(new Tariff())
            .balance(BigDecimal.ZERO)
            .build();
    }

    private SavingsAccount buildMockSavingsAccountWithBalance(BigDecimal balance) {
        SavingsAccount account = buildMockSavingsAccount();
        account.setBalance(balance);
        return account;
    }

    private SavingsAccount buildMockSavingsAccountWithTariff(Tariff tariff) {
        SavingsAccount account = buildMockSavingsAccount();
        account.setTariff(tariff);
        return account;
    }
}
