package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
class TariffServiceIntTest {
    @Autowired
    private TariffService service;
    @Autowired
    private TariffRepository repository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
    private static final UUID SAVINGS_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Long TARIFF_ID = 1L;
    private static final Long UNKNOWN_TARIFF_ID = 10L;
    private static final String BASE_TARIFF = "BASE";
    private static final String NEW_TARIFF_TYPE = "PRO";

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
    @DisplayName("Успешное создание нового тарифа")
    void positive_shouldCreateTariff() {
        TariffDto expected = createTariffDto(null, NEW_TARIFF_TYPE);

        TariffDto actual = service.create(expected);

        assertTrue(repository.findById(actual.id()).isPresent());
        assertNotNull(actual.createdAt());
        assertEquals(expected.currentRate(), actual.currentRate());
    }

    @Test
    @DisplayName("Успешное обновление тарифа")
    void positive_shouldUpdateTariff() {
        TariffDto expected = createTariffDto(TARIFF_ID, BASE_TARIFF);

        TariffDto actual = service.update(expected);

        assertEquals(expected.currentRate(), actual.currentRate());
    }

    @Test
    @DisplayName("Успешное получение всех тарифов")
    void positive_shouldFindAll() {
        List<TariffDto> actual = service.findAll();

        assertFalse(actual.isEmpty());
    }

    @Test
    @DisplayName("Успешное изменение текущего тарифа для счета")
    void positive_shouldCreateTariffHistory() {
        Account account = createAccount();

        TariffHistory actual = service.createTariffHistory(account, TARIFF_ID);

        assertNotNull(actual);
    }

    @Test
    @DisplayName("Ошибка обновления тарифа по id - тариф не найден")
    void negative_whenUpdatedTariffNotFound_throwsError() {
        String expectedMessage = "Tariff not found by id = " + UNKNOWN_TARIFF_ID;
        TariffDto expected = createTariffDto(UNKNOWN_TARIFF_ID, BASE_TARIFF);

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.update(expected)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // -----------------------------

    private TariffDto createTariffDto(Long id, String type) {
        return TariffDto.builder()
                .id(id)
                .type(type)
                .currentRate(BigDecimal.TEN)
                .build();
    }

    private Account createAccount() {
        return Account.builder()
                .id(SAVINGS_ID)
                .build();
    }
}