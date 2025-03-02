package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class BalanceServiceIntegrationTest {


    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("initDB.sql");

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceRepository balanceRepository;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Test scenario 1")
    public void testScenario1() {
        BalanceResponseDto resultBalance;
        BigDecimal actualBalance;
        BigDecimal authorizedBalance;
        BigDecimal checkedActualBalance;
        BigDecimal checkedAuthorizedBalance;

        BigDecimal popUpSum = new BigDecimal("100.00");
        BigDecimal writeOffSum = new BigDecimal("50.98");
        BigDecimal holdSum = new BigDecimal("10.11");
        Account account = Account.builder().id(1L).build();
        Balance balance = Balance.builder().account(account).build();

        checkedActualBalance = popUpSum;
        checkedAuthorizedBalance = BigDecimal.ZERO;

        resultBalance = balanceService.topUpBalance(1L, popUpSum);
        actualBalance = resultBalance.actualBalance();
        authorizedBalance = resultBalance.authorizedBalance();
        Assertions.assertEquals(0, checkedActualBalance.compareTo(actualBalance));
        Assertions.assertEquals(0, checkedAuthorizedBalance.compareTo(authorizedBalance));

        checkedActualBalance = checkedActualBalance.subtract(writeOffSum);

        resultBalance = balanceService.writeOffFunds(1L, writeOffSum);
        actualBalance = resultBalance.actualBalance();
        authorizedBalance = resultBalance.authorizedBalance();
        Assertions.assertEquals(0, checkedActualBalance.compareTo(actualBalance));
        Assertions.assertEquals(0, checkedAuthorizedBalance.compareTo(authorizedBalance));

        checkedActualBalance = checkedActualBalance.subtract(holdSum);
        checkedAuthorizedBalance = holdSum;

        resultBalance = balanceService.holdFunds(1L, holdSum);
        actualBalance = resultBalance.actualBalance();
        authorizedBalance = resultBalance.authorizedBalance();
        Assertions.assertEquals(0, checkedActualBalance.compareTo(actualBalance));
        Assertions.assertEquals(0, checkedAuthorizedBalance.compareTo(authorizedBalance));

        checkedActualBalance = checkedActualBalance.add(holdSum);
        checkedAuthorizedBalance = BigDecimal.ZERO;

        resultBalance = balanceService.releaseFunds(1L, holdSum);
        actualBalance = resultBalance.actualBalance();
        authorizedBalance = resultBalance.authorizedBalance();
        Assertions.assertEquals(0, checkedActualBalance.compareTo(actualBalance));
        Assertions.assertEquals(0, checkedAuthorizedBalance.compareTo(authorizedBalance));

        checkedActualBalance = checkedActualBalance.subtract(holdSum);

        resultBalance = balanceService.holdFunds(1L, holdSum);
        resultBalance = balanceService.writeOffHeldFunds(1L, holdSum);
        actualBalance = resultBalance.actualBalance();
        authorizedBalance = resultBalance.authorizedBalance();
        Assertions.assertEquals(0, checkedActualBalance.compareTo(actualBalance));
        Assertions.assertEquals(0, checkedAuthorizedBalance.compareTo(authorizedBalance));
    }

    @Test
    @DisplayName("Test optimistic lock")
    public void testOptimisticLock() {
        Balance balance1 = balanceRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Cannot find balance!"));
        Balance balance2 = balanceRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Cannot find balance!"));

        balance1.setActualBalance(new BigDecimal("100.00"));
        balance2.setActualBalance(new BigDecimal("200.00"));

        balanceRepository.save(balance1);
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            balanceRepository.save(balance2);
        });
    }


}
