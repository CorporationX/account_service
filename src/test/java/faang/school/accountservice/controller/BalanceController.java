package faang.school.accountservice.controller;

import faang.school.accountservice.AccountServiceApplicationTests;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static faang.school.accountservice.enums.AccountStatus.ACTIVE;
import static faang.school.accountservice.enums.AccountType.INDIVIDUAL_CURRENT;
import static faang.school.accountservice.enums.Currency.RUB;
import static org.junit.Assert.*;

@DirtiesContext
@SpringBootTest
public class BalanceController extends AccountServiceApplicationTests {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    private UUID accountId;
    private UUID balanceId;

    @BeforeEach
    void setUp() {
        Object AccountType;
        Account account = Account.builder()
                .accountNumber("ACC-" + System.currentTimeMillis())
                .userId(1L)
                .type(INDIVIDUAL_CURRENT)
                .currency(RUB)
                .status(ACTIVE)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account savedAccount = accountRepository.save(account);
        accountId = savedAccount.getId();

        Balance balance = Balance.builder()
                .account(savedAccount)
                .actualBalance(new BigDecimal("1000.00"))
                .authorizedBalance(BigDecimal.ZERO)
                .build();

        Balance savedBalance = balanceRepository.save(balance);
        balanceId = savedBalance.getId();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testOptimisticLockExceptionWhenVersionChanged() {


        Balance balance1 = balanceService.getBalance(accountId);
        Long originalVersion = balance1.getVersion();

        balanceRepository.findById(balanceId).ifPresent(b -> {
            b.setActualBalance(b.getActualBalance().add(new BigDecimal("500.00")));
            balanceRepository.save(b);
        });

        try {
            Balance staleBalance = Balance.builder()
                    .id(balanceId)
                    .account(balance1.getAccount())
                    .actualBalance(new BigDecimal("900.00"))
                    .authorizedBalance(new BigDecimal("100.00"))
                    .version(originalVersion)
                    .build();

            balanceRepository.save(staleBalance);

            fail("Expected OptimisticLockingFailureException");

        } catch (OptimisticLockingFailureException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testOptimisticLockingWhenConcurrentUpdates() throws InterruptedException {
        BigDecimal amount1 = new BigDecimal("100.00");
        BigDecimal amount2 = new BigDecimal("200.00");

        Balance originalBalance = balanceRepository.findById(balanceId).orElseThrow();
        Long originalVersion = originalBalance.getVersion();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<?> future1 = executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                balanceService.authorize(accountId, amount1);
            });

            Future<?> future2 = executor.submit(() -> {
                // Второе обновление - еще одна авторизация
                balanceService.authorize(accountId, amount2);
            });

            future1.get();
            future2.get();

            Balance updatedBalance = balanceRepository.findById(balanceId).orElseThrow();

            assertNotEquals(originalVersion, updatedBalance.getVersion());

            BigDecimal expectedActualBalance = new BigDecimal("1000.00")
                    .subtract(amount1)
                    .subtract(amount2);

            assertTrue(updatedBalance.getActualBalance().compareTo(expectedActualBalance) >= 0);
            assertTrue(updatedBalance.getAuthorizedBalance().compareTo(amount1.add(amount2)) >= 0);

        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof OptimisticLockingFailureException ||
                    e.getCause().getMessage().contains("optimistic lock") ||
                    e.getCause().getMessage().contains("version"));
        } finally {
            executor.shutdown();
        }
    }
}
