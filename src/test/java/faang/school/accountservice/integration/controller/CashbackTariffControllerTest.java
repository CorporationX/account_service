package faang.school.accountservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.integration.IntegrationTestBase;
import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.CreateTariffMerchantDto;
import faang.school.accountservice.model.cashback.CreateTariffOperationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CashbackTariffControllerTest extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCashbackTariffSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/cashback-tariffs/1")
                        .header("x-user-id", "1"))
                .andExpectAll(content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Test name 1"),
                        jsonPath("$.createdAt").value("2024-11-01T12:00:00"),
                        jsonPath("$.operations[0].id").value(1),
                        jsonPath("$.operations[0].operationType").value("PURCHASE"),
                        jsonPath("$.operations[0].percentage").value(1.20),
                        jsonPath("$.operations[1].id").value(2),
                        jsonPath("$.operations[1].operationType").value("RESTAURANT"),
                        jsonPath("$.operations[1].percentage").value(1.30),
                        jsonPath("$.merchants[0].id").value(1),
                        jsonPath("$.merchants[0].merchantName").value("Test Merchant 1"),
                        jsonPath("$.merchants[0].percentage").value(5.0),
                        jsonPath("$.merchants[1].id").value(2),
                        jsonPath("$.merchants[1].merchantName").value("Test Merchant 2"),
                        jsonPath("$.merchants[1].percentage").value(3.5))
                .andDo(print());

        //TODO:Добавить проверку базы
    }

    @Test
    void createCashbackTariff() throws Exception {
        var merchant = CreateTariffMerchantDto.builder()
                .id(1L)
                .percentage(BigDecimal.valueOf(5.3))
                .build();
        var merchant2 = CreateTariffMerchantDto.builder()
                .id(2L)
                .percentage(BigDecimal.valueOf(3.1))
                .build();

        var operation = CreateTariffOperationDto.builder()
                .id(1L)
                .percentage(BigDecimal.valueOf(3.75))
                .build();
        var operation2 = CreateTariffOperationDto.builder()
                .id(3L)
                .percentage(BigDecimal.valueOf(4.1))
                .build();

        var cashbackTariffDto = CreateCashbackTariffDto.builder()
                .name("Test Cashback Tariff 3")
                .merchants(List.of(merchant, merchant2))
                .operations(List.of(operation, operation2))
                .build();

        var body = objectMapper.writeValueAsString(cashbackTariffDto);

        mockMvc.perform(post("/api/v1/cashback-tariffs")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "1"))
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(3),
                        jsonPath("$.name").value("Test Cashback Tariff 3"),
                        jsonPath("$.createdAt").isNotEmpty(),
                        jsonPath("$.operations[0].id").value(1),
                        jsonPath("$.operations[0].operationType").value("PURCHASE"),
                        jsonPath("$.operations[0].percentage").value(3.75),
                        jsonPath("$.operations[1].id").value(3),
                        jsonPath("$.operations[1].operationType").value("TRAVEL"),
                        jsonPath("$.operations[1].percentage").value(4.1),
                        jsonPath("$.merchants[0].id").value(1),
                        jsonPath("$.merchants[0].merchantName").value("Test Merchant 1"),
                        jsonPath("$.merchants[0].percentage").value(5.3),
                        jsonPath("$.merchants[1].id").value(2),
                        jsonPath("$.merchants[1].merchantName").value("Test Merchant 2"),
                        jsonPath("$.merchants[1].percentage").value(3.1))
                .andDo(print());

        //TODO:Добавить проверку базы
    }
}