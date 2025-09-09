package faang.school.accountservice.service;

import faang.school.accountservice.config.property.BalanceProps;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/db/script/interest_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/script/interest_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class InterestServiceIntTest {
    @Autowired
    private InterestService service;
    @Autowired
    private SavingsAccountRepository savingsAccountRepository;
    @Autowired
    private BalanceProps balanceProps;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
    private static final UUID SAVINGS_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final BigDecimal CURRENT_RATE = BigDecimal.valueOf(2);

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Успешное начисление процентов на баланс")
    void positive_shouldAccrueInterest() {
        SavingsAccount expectedSavings = savingsAccountRepository.findById(SAVINGS_ID).get();
        BigDecimal expected = calculateNewBalance(expectedSavings.getBalance(), CURRENT_RATE);

        service.accrueInterest();
        SavingsAccount actualSavings = savingsAccountRepository.findById(SAVINGS_ID).get();
        BigDecimal actual = actualSavings.getBalance();

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "22222222-2222-2222-2222-222222222222",
            "33333333-3333-3333-3333-333333333333",
            "44444444-4444-4444-4444-444444444444"})
    @DisplayName("Успешное НЕ начисление процентов на баланс - счет не подпадает по критериям")
    void positive_shouldNotAccrueInterest(String id) {
        UUID accountId = UUID.fromString(id);
        SavingsAccount expectedSavings = savingsAccountRepository.findById(accountId).get();
        BigDecimal expected = expectedSavings.getBalance();

        service.accrueInterest();
        SavingsAccount actualSavings = savingsAccountRepository.findById(accountId).get();
        BigDecimal actual = actualSavings.getBalance();

        assertEquals(expected, actual);
    }

    // ---------------------------------

    private BigDecimal calculateNewBalance(BigDecimal balance, BigDecimal rate) {
        BigDecimal interestAmount = calculateDailyInterest(balance, rate);
        return balance.add(interestAmount);
    }

    private BigDecimal calculateDailyInterest(BigDecimal balance, BigDecimal rate) {
        BigDecimal daysOfYear = BigDecimal.valueOf(Year.now().isLeap() ? 366 : 365);
        return balance
                .multiply(rate.divide(daysOfYear, balanceProps.divideScale(), balanceProps.roundingMode()))
                .setScale(balanceProps.scale(), balanceProps.roundingMode());
    }
}