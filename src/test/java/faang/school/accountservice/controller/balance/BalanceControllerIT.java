package faang.school.accountservice.controller.balance;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.dto.Money;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BalanceControllerIT extends BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BalanceControllerIT.class);

    @Autowired
    private BalanceService balanceService;

    @Test
    @Sql(scripts = {"/test-data-accounts-balances.sql", "/test-data-auth-payments.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testAcceptPayment_OptimisticLockException() throws InterruptedException, ExecutionException {
        final UUID paymentId = UUID.fromString("2f04fdc1-8327-4cb0-b102-f59ff9cbf5d7");
        Money money = new Money(BigDecimal.valueOf(20), Currency.USD);
        Money moneyParal = new Money(BigDecimal.valueOf(50), Currency.USD);

        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        final AtomicReference<Throwable> exceptionHolder = new AtomicReference<>(null);

        Future<Void> future1 = executor.submit(() -> {
            try {
                barrier.await();

                balanceService.acceptPayment(paymentId, money);
            } catch (ObjectOptimisticLockingFailureException e) {

                logger.info("OptimisticLockException caught in thread 1 ");
                exceptionHolder.set(e);
            }
            return null;
        });

        Future<Void> future2 = executor.submit(() -> {
            try {
                barrier.await();

                balanceService.acceptPayment(paymentId, moneyParal);
            } catch (OptimisticLockingFailureException e) {

                logger.info("OptimisticLockException caught in thread 2 ");
                exceptionHolder.set(e);
            }
            return null;
        });

        future1.get();
        future2.get();

        executor.shutdown();

        Assertions.assertNotNull(exceptionHolder.get(),
                "Expected OptimisticLockingFailureException to be thrown in one of the threads.");
    }
}