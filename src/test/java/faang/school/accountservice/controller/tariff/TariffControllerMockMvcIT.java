package faang.school.accountservice.controller.tariff;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.repository.rate.RateRepository;
import faang.school.accountservice.repository.tariff.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class TariffControllerMockMvcIT {

    private static final long ID = 1L;
    private static final String RATE_HISTORY = "[1.0]";
    private static final String NEW_RATE_HISTORY = "[1.0,2.0]";
    private static final Double INTEREST_RATE = 1.0;
    private static final Double NEW_INTEREST_RATE = 2.0;
    private static final String TARIFF_NAME = "tariff";
    private static final String WRONG_TARIFF_NAME = "wrong";
    private TariffRequestDto requestDto;
    private Tariff tariff;
    private Rate rate;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private RateRepository rateRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:14")
                    .withInitScript("create_schema_test.sql");

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeEach
    public void init() {
        requestDto = TariffRequestDto.builder()
                .tariffName(TARIFF_NAME)
                .interestRate(INTEREST_RATE)
                .build();

        rate = Rate.builder()
                .id(ID)
                .interestRate(INTEREST_RATE)
                .build();
        rateRepository.save(rate);

        tariff = Tariff.builder()
                .tariffName(TARIFF_NAME)
                .rate(rate)
                .rateHistory(RATE_HISTORY)
                .build();
        tariffRepository.save(tariff);
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешное создание тарифа")
        public void whenCreateTariffThenReturnTariffDto() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post("/v1/tariffs")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            TariffDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TariffDto.class);

            assertNotNull(result);
            assertEquals(requestDto.getTariffName(), result.getTariffName());
            assertEquals(requestDto.getInterestRate(), result.getRateDto().getInterestRate());
        }

        @Test
        @DisplayName("Успешное получение списка тарифов")
        public void whenGetAllTariffsThenReturnListTariffDtos() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/v1/tariffs")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            List<TariffDto> result = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(TARIFF_NAME, result.get(0).getTariffName());
            assertEquals(rate.getInterestRate(), result.get(0).getRateDto().getInterestRate());
            assertEquals(RATE_HISTORY, result.get(0).getRateHistory());
        }

        @Test
        @DisplayName("Успешное изменение процентной ставки существующего тарифа")
        public void whenUpdateTariffRateThenReturnUpdatedTariffDto() throws Exception {
            requestDto.setInterestRate(NEW_INTEREST_RATE);

            MvcResult mvcResult = mockMvc.perform(put("/v1/tariffs")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            TariffDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TariffDto.class);

            assertNotNull(result);
            assertEquals(requestDto.getTariffName(), result.getTariffName());
            assertEquals(requestDto.getInterestRate(), result.getRateDto().getInterestRate());
            assertEquals(NEW_RATE_HISTORY, result.getRateHistory());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка если TariffRequestDto пустое")
        public void whenCreateTariffWithTariffRequestDtoIsNullThenThrowException() throws Exception {
            mockMvc.perform(post("/v1/tariffs")
                            .header("x-user-id", 1)
                            .content("{}"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Ошибка если TariffRequestDto не содержит значения в поле interestRate")
        public void whenCreateTariffWithFieldInterestRateIsNullThenThrowException() throws Exception {
            requestDto.setInterestRate(null);

            mockMvc.perform(post("/v1/tariffs")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Ошибка если TariffRequestDto не содержит значения в поле tariffName")
        public void whenCreateTariffWithFieldTariffNameIsNullThenThrowException() throws Exception {
            requestDto.setTariffName(null);

            mockMvc.perform(post("/v1/tariffs")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Ошибка если тариф с названием, переданным в requestDto, не существует в БД")
        public void whenUpdateTariffRateWithWrongTariffNameThenThrowException() throws Exception {
            requestDto.setTariffName(WRONG_TARIFF_NAME);

            mockMvc.perform(put("/v1/tariffs")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError());
        }
    }
}