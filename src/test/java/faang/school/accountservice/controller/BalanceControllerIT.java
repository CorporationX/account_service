package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.enums.ChangeBalanceType;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/db/account/test_balance_schema.sql")
public class BalanceControllerIT extends BaseContextTest {

    @Test
    public void testGetBalanceSuccess() throws Exception {
        String response = mockMvc.perform(get("/api/v1/balance/1").header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(0, balanceDto.actualBalance().compareTo(BigDecimal.valueOf(100)));
        assertEquals(0, balanceDto.authBalance().compareTo(BigDecimal.valueOf(50)));
        assertEquals(balanceDto.accountId(), 1);
    }

    @Test
    public void testGetBalanceFailWithNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/balance/2").header("x-user-id", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Not found balance by id: 2"));
    }

    @Test
    public void testGetBalanceByAccountIdSuccess() throws Exception {
        String response = mockMvc.perform(get("/api/v1/balance").header("x-user-id", 1)
                        .param("accountId", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(0, balanceDto.actualBalance().compareTo(BigDecimal.valueOf(100)));
        assertEquals(0, balanceDto.authBalance().compareTo(BigDecimal.valueOf(50)));
        assertEquals(balanceDto.accountId(), 1);
    }

    @Test
    public void testGetBalanceByAccountIdFailWithNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/balance").header("x-user-id", 1)
                        .param("accountId", String.valueOf(3L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("The balance was not found for account with id: 3"));
    }

    @Test
    public void testCreateBalanceSuccess() throws Exception {
        String response = mockMvc.perform(post("/api/v1/balance")
                        .header("x-user-id", 1)
                        .param("accountId", String.valueOf(2L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(balanceDto.authBalance(), BigDecimal.ZERO);
        assertEquals(balanceDto.actualBalance(), BigDecimal.ZERO);
        assertEquals(2L, balanceDto.id());
        assertEquals(2L, balanceDto.accountId());
    }

    @Test
    public void testCreateBalanceFailWithNotFoundAccountId() throws Exception {
        mockMvc.perform(post("/api/v1/balance")
                        .header("x-user-id", 1)
                        .param("accountId", String.valueOf(3L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found account id: 3"));
    }

    @Test
    public void testCreateBalanceFailWithExistsBalance() throws Exception {
        mockMvc.perform(post("/api/v1/balance")
                        .header("x-user-id", 1)
                        .param("accountId", String.valueOf(1L)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Balance is already exist for account ID: 1"));
    }

    @Test
    public void testUpdateBalanceFailWhenWithdrawActualMoreThanAuth() throws Exception {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .changeBalanceType(ChangeBalanceType.ACTUAL)
                .operationType(OperationType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(100))
                .build();

        mockMvc.perform(put("/api/v1/balance/1").header("x-user-id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Insufficient funds to change balance, " +
                                "auth balance: 50.00, balance: 100.00"));
    }

    @Test
    public void testUpdateBalanceFailWithdrawWithNotEnoughMoney() throws Exception {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .changeBalanceType(ChangeBalanceType.AUTHORIZATION)
                .operationType(OperationType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(1000))
                .build();

        mockMvc.perform(put("/api/v1/balance/1").header("x-user-id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Insufficient funds to withdraw, balance: 50.00, amount: 1000"));
    }

    @Test
    public void testUpdateBalanceActualReplenishmentSuccess() throws Exception {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .changeBalanceType(ChangeBalanceType.ACTUAL)
                .operationType(OperationType.REPLENISHMENT)
                .amount(BigDecimal.valueOf(1000))
                .build();

        String response = mockMvc.perform(put("/api/v1/balance/1").header("x-user-id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(0, balanceDto.actualBalance().compareTo(BigDecimal.valueOf(1100)));
    }

    @Test
    public void testUpdateBalanceAuthReplenishmentSuccess() throws Exception {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .changeBalanceType(ChangeBalanceType.AUTHORIZATION)
                .operationType(OperationType.REPLENISHMENT)
                .amount(BigDecimal.valueOf(10))
                .build();

        String response = mockMvc.perform(put("/api/v1/balance/1").header("x-user-id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(0, balanceDto.authBalance().compareTo(BigDecimal.valueOf(60)));
    }

    @Test
    public void testUpdateBalanceActualWithdrawSuccess() throws Exception {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .changeBalanceType(ChangeBalanceType.ACTUAL)
                .operationType(OperationType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(10))
                .build();

        String response = mockMvc.perform(put("/api/v1/balance/1").header("x-user-id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(0, balanceDto.actualBalance().compareTo(BigDecimal.valueOf(90)));
    }

    @Test
    public void testUpdateBalanceAuthWithdrawSuccess() throws Exception {
        AmountChangeRequest request = AmountChangeRequest.builder()
                .changeBalanceType(ChangeBalanceType.AUTHORIZATION)
                .operationType(OperationType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(50))
                .build();

        String response = mockMvc.perform(put("/api/v1/balance/1").header("x-user-id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceDto balanceDto = objectMapper.readValue(response, BalanceDto.class);

        assertEquals(0, balanceDto.authBalance().compareTo(BigDecimal.ZERO));
    }
}
