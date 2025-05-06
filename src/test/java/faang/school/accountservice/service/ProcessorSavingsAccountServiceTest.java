package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessorSavingsAccountServiceTest {

    @InjectMocks
    private ProcessorSavingsAccountService processorSavingsAccountService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private TariffRepository tariffRepository;

    @BeforeEach
    void setUp() {
        processorSavingsAccountService = new ProcessorSavingsAccountService(savingsAccountRepository, tariffRepository);
    }

    @Test
    void testPositiveProcessAccount() {
        Account account = createAccount();
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal expectedBalance = new BigDecimal("1050.00");
        SavingsAccount savingsAccount = createSavingsAccount(account, initialBalance);
        String tariffType = "STANDARD";
        BigDecimal rate = new BigDecimal("5.00");
        when(savingsAccountRepository.findLatestTariffTypeNameByAccountId(savingsAccount.getAccountId()))
                .thenReturn(Optional.of(tariffType));
        when(tariffRepository.findLatestRateByTariffTypeName(tariffType))
                .thenReturn(Optional.of(rate));

        processorSavingsAccountService.processSingleAccount(savingsAccount);

        verify(savingsAccountRepository).save(savingsAccount);
        BigDecimal balanceAfter = savingsAccount.getBalance();
        assertEquals(balanceAfter, expectedBalance);
        assertNotNull(savingsAccount.getLastInterestAccrualAt());
    }

    private Account createAccount() {
        return Account.builder()
                .id(1L)
                .build();
    }

    private SavingsAccount createSavingsAccount(Account account, BigDecimal balance) {
        return SavingsAccount.builder()
                .account(account)
                .balance(balance)
                .lastInterestAccrualAt(null)
                .build();
    }
}
