package faang.school.accountservice.service;

import faang.school.accountservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/db/script/rate_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/script/rate_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RateServiceIntTest {
    @Autowired
    private RateService service;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
    private static final UUID SAVINGS_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID UNKNOWN_SAVINGS_ID = UUID.fromString("10101010-1010-0101-1010-010101010101");

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
    @DisplayName("Успешное получение текущей ставки по id счета")
    void positive_shouldFindCurrentRateByAccountId() {
        BigDecimal actual = service.findCurrentRateByAccountId(SAVINGS_ID);

        assertNotNull(actual);
        assertEquals("2.00", actual.toString());
    }

    @Test
    @DisplayName("Ошибка получения текущей ставки по id счета - ставка не найдена")
    void negative_whenRateNotFoundByAccountId_throwsError() {
        String expectedMessage = "Current interest rate not found by account id = " + UNKNOWN_SAVINGS_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                     () -> service.findCurrentRateByAccountId(UNKNOWN_SAVINGS_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}