package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountInterestTest {

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private BalanceJpaRepository balanceRepository;

    @InjectMocks
    private SavingsAccountInterest savingsAccountInterest;

    private SavingsAccount savingsAccount1;
    private SavingsAccount savingsAccount2;

    @BeforeEach
    public void setUp() {
        Balance balance1 = new Balance();
        Account account1 = Account.builder()
                .savingsAccount(savingsAccount1)
                .balance(balance1)
                .build();

        Tariff tariff1 = Tariff.builder()
                .currentRate(BigDecimal.valueOf(1L)).build();

        balance1.setCurFactBalance(BigDecimal.valueOf(1000L));

        savingsAccountInterest.setPoolThreads(10);
        savingsAccountInterest.setAmountHours(24);
        savingsAccountInterest.setAmountDays(365);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthLater = now.plusHours(-24);

        savingsAccount1 = SavingsAccount.builder()
                .account(account1)
                .tariff(tariff1)
                .id(1L)
                .lastDateBeforeInterest(oneMonthLater)
                .build();

        savingsAccount2 = SavingsAccount.builder()
                .id(2L)
                .account(account1)
                .tariff(tariff1)
                .lastDateBeforeInterest(oneMonthLater)
                .build();

        List<SavingsAccount> accounts = Arrays.asList(savingsAccount1, savingsAccount2);

        when(savingsAccountRepository.findAll()).thenReturn(accounts);

    }

    @Test
    public void testInterestAccrual() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(savingsAccountRepository).save(any(SavingsAccount.class));

        savingsAccountInterest.interestAccrual();
        latch.await();

        verify(savingsAccountRepository, times(1)).findAll();
        verify(savingsAccountRepository, times(1)).save(savingsAccount1);
        verify(savingsAccountRepository, times(1)).save(savingsAccount2);
    }
}

