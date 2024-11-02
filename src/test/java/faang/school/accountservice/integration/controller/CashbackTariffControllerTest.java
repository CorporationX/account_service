package faang.school.accountservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.integration.IntegrationTestBase;
import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.MerchantDto;
import faang.school.accountservice.model.cashback.OperationDto;
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
                        jsonPath("$.operations[1].percentage").value(1.30)).
                andDo(print());
    }

    @Test
    void createCashbackTariff() throws Exception {
        var merchant = MerchantDto.builder()
                .id(1L)
                .percentage(BigDecimal.ONE)
                .build();
        var operation = OperationDto.builder()
                .build();
        var cashbackTariffDto = CreateCashbackTariffDto.builder()
                .name("Test Cashback Tariff 2")
                .merchants(List.of(merchant))
                .operations(List.of(operation))
                .build();

        var body = objectMapper.writeValueAsString(cashbackTariffDto);

        mockMvc.perform(post("/api/v1/cashback-tariffs")
                .content(body).contentType(MediaType.APPLICATION_JSON)
                .header("x-user-id", "1"))
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(3),
                        jsonPath("$.name").value("Test Cashback Tariff 2"),
                        jsonPath("$.createdAt").isNotEmpty());
    }
}