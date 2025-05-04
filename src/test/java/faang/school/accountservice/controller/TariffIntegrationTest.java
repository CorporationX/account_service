package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.TariffUpdateRequest;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffRate;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class TariffIntegrationTest {

    private static final String USER_PARAMETER = "x-user-id";
    private static final String TARIFF_METHODS_URL = "/tariffs";

    private final int userId = 100;
    private final Long id = 1L;
    private final String firstTariffName = "Gold";
    private final BigDecimal firstRate = new BigDecimal("5.00");
    private final BigDecimal secondRate = new BigDecimal("3.00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TariffRepository tariffRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @AfterEach
    void tearDown() {
        tariffRepository.deleteAll();
    }

    @Test
    void testNegativeAddTariffWhenTariffAlreadyExists() throws Exception {
        tariffRepository.save(createTariff(firstTariffName, firstRate));

        mockMvc.perform(post(TARIFF_METHODS_URL)
                        .header(USER_PARAMETER, userId)
                        .param("typeName", firstTariffName)
                        .param("rate", firstRate.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void testPositiveAddTariff() throws Exception {
        mockMvc.perform(post(TARIFF_METHODS_URL)
                        .header(USER_PARAMETER, userId)
                        .param("typeName", firstTariffName)
                        .param("rate", firstRate.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeName").value(firstTariffName))
                .andExpect(jsonPath("$.activeRate").value(firstRate.toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testNegativeUpdateTariffWhenTariffNotFound() throws Exception {
        TariffUpdateRequest request = createTariffRequest(id, firstRate);

        mockMvc.perform(put(TARIFF_METHODS_URL)
                        .header(USER_PARAMETER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPositiveUpdateTariff() throws Exception {
        Tariff tariff = createTariff(firstTariffName, firstRate);
        tariffRepository.save(tariff);
        TariffUpdateRequest request = createTariffRequest(id, firstRate);

        mockMvc.perform(put(TARIFF_METHODS_URL)
                        .header(USER_PARAMETER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetAllTariffsWhenTariffsNotFound() throws Exception {
        mockMvc.perform(get(TARIFF_METHODS_URL)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testPositiveGetAllTariffsWhenTariffsFound() throws Exception {
        String secondTariffName = "Silver";
        List<Tariff> tariffs = List.of(
                createTariff(firstTariffName, firstRate), createTariff(secondTariffName, secondRate)
        );
        tariffRepository.saveAll(tariffs);

        mockMvc.perform(get(TARIFF_METHODS_URL)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].typeName").value(tariffs.get(0).getTypeName()))
                .andExpect(jsonPath("$[0].activeRate").value(tariffs.get(0).getRates().get(0).getRate()))
                .andExpect(jsonPath("$[1].typeName").value(tariffs.get(1).getTypeName()))
                .andExpect(jsonPath("$[1].activeRate").value(tariffs.get(1).getRates().get(0).getRate()));
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private TariffUpdateRequest createTariffRequest(Long id, BigDecimal rate) {
        return TariffUpdateRequest.builder()
                .id(id)
                .typeName("Gold")
                .rate(rate)
                .build();
    }

    private Tariff createTariff(String name, BigDecimal rate) {
        Tariff tariff = Tariff.builder()
                .typeName(name)
                .build();

        List<TariffRate> rates = new ArrayList<>();
        TariffRate tariffRate = createRate(rate, tariff);
        rates.add(tariffRate);
        tariff.setRates(rates);

        return tariff;
    }

    private TariffRate createRate(BigDecimal rate, Tariff tariff) {
        return TariffRate.builder()
                .rate(rate)
                .tariff(tariff)
                .build();
    }
}
