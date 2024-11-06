package faang.school.accountservice.service.savings_account;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.entity.savings_account.TariffToSavingAccountBinding;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.tariff.TariffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.enums.account.AccountStatus.ACTIVE;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccount;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.SavingsAccountFabric.buildSavingsAccount;
import static faang.school.accountservice.util.fabrics.TariffFabric.buildTariff;
import static faang.school.accountservice.util.fabrics.TariffToSavingAccountBindingFabric.buildTariffToSavingAccountBinding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID SAVINGS_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID BALANCE_ID = UUID.randomUUID();
    private static final Long TARIFF_ID = 1L;
    private static final Long NEW_TARIFF_ID = 2L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(1000.0);
    private static final Double RATE = 0.01;

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private TariffService tariffService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Test
    void testOpenSavingsAccount_Success() {
        SavingsAccount savingsAccount = buildSavingsAccount();
        Account account = buildAccount();
        Balance balance = buildBalance();
        Tariff tariff = buildTariff();

        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(account);
        when(balanceService.createOrGetBalanceWithAmount(account, AMOUNT)).thenReturn(balance);
        when(tariffService.findById(TARIFF_ID)).thenReturn(tariff);
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        SavingsAccount result = savingsAccountService.openSavingsAccount(savingsAccount, ACCOUNT_ID, TARIFF_ID, AMOUNT);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(savingsAccount);

        verify(savingsAccountRepository).save(any(SavingsAccount.class));
    }

    @Test
    void testGetById_Success() {
        Account account = buildAccount(ACCOUNT_ID);
        SavingsAccount savingsAccount = buildSavingsAccount(SAVINGS_ACCOUNT_ID, account);
        Balance balance = buildBalance(BALANCE_ID, 0, AMOUNT.doubleValue());

        when(savingsAccountRepository.findById(eq(SAVINGS_ACCOUNT_ID))).thenReturn(Optional.of(savingsAccount));
        when(savingsAccountRepository.getCurrentRate(eq(SAVINGS_ACCOUNT_ID))).thenReturn(RATE);
        when(balanceService.findByAccountId(ACCOUNT_ID)).thenReturn(balance);

        SavingsAccount result = savingsAccountService.getById(SAVINGS_ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(RATE, result.getCurrentRate());
        assertEquals(AMOUNT, result.getAmount());
    }

    @Test
    void testGetById_Exception() {
        when(savingsAccountRepository.findById(eq(SAVINGS_ACCOUNT_ID))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                savingsAccountService.getById(SAVINGS_ACCOUNT_ID)
        );
    }

    @Test
    void testGetAllActive_Success() {
        SavingsAccount savingsAccount = buildSavingsAccount(SAVINGS_ACCOUNT_ID);

        when(savingsAccountRepository.findByAccount_Status(eq(ACTIVE))).thenReturn(List.of(savingsAccount));

        List<SavingsAccount> result = savingsAccountService.getAllActive();

        assertEquals(1, result.size());
        assertEquals(SAVINGS_ACCOUNT_ID, result.get(0).getId());
    }

    @Test
    void testUpdateTariff_Success() {
        Account account = buildAccount(ACCOUNT_ID);
        SavingsAccount savingsAccount = buildSavingsAccount(SAVINGS_ACCOUNT_ID, account);
        Tariff tariff = buildTariff(TARIFF_ID);
        TariffToSavingAccountBinding tariffToSavingAccountBinding = buildTariffToSavingAccountBinding(tariff, savingsAccount);
        savingsAccount.getTariffToSavingAccountBindings().add(tariffToSavingAccountBinding);
        Balance balance = buildBalance(BALANCE_ID, 0, AMOUNT.doubleValue());

        when(savingsAccountRepository.findById(eq(SAVINGS_ACCOUNT_ID))).thenReturn(Optional.of(savingsAccount));
        when(tariffService.findById(NEW_TARIFF_ID)).thenReturn(tariff);
        when(savingsAccountRepository.getCurrentRate(eq(SAVINGS_ACCOUNT_ID))).thenReturn(RATE);
        when(balanceService.findByAccountId(ACCOUNT_ID)).thenReturn(balance);
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        SavingsAccount result = savingsAccountService.updateTariff(SAVINGS_ACCOUNT_ID, NEW_TARIFF_ID);

        assertEquals(2, result.getTariffToSavingAccountBindings().size());
        assertEquals(TARIFF_ID, result.getTariffToSavingAccountBindings().get(1).getTariff().getId());
    }

    @Test
    void testAccrueBalanceForSavingsAccount_Success() {
        Account account = buildAccount(ACCOUNT_ID);
        SavingsAccount savingsAccount = buildSavingsAccount(SAVINGS_ACCOUNT_ID, account);
        LocalDate now = LocalDate.now();

        when(savingsAccountRepository.getCurrentRate(eq(SAVINGS_ACCOUNT_ID))).thenReturn(RATE);
        doNothing().when(balanceService).multiplyCurrentBalance(any(UUID.class), anyDouble());
        when(savingsAccountRepository.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        savingsAccountService.accrueBalanceForSavingsAccount(savingsAccount, now);

        verify(savingsAccountRepository).save(any(SavingsAccount.class));
    }
}