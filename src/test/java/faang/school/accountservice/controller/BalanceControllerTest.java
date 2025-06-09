package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.BalanceOperationDto;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.service.interfaces.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@AutoConfigureMockMvc
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private UserContext userContext;

    @Test
    void testCreateBalanceWhenReturnCreated() throws Exception {
        long accountId = 1L;
        BalanceResponseDto dto = new BalanceResponseDto(10L, accountId,
                BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());

        when(balanceService.createBalance(accountId)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/accounts/{accountId}/balance", accountId)
                        .header("x-user-id", "123"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/10")))
                .andExpect(jsonPath("$.balanceId").value(10L));
    }

    @Test
    void testGetBalanceWhenReturnBalance() throws Exception {
        long accountId = 1L;
        BalanceResponseDto responseDto = new BalanceResponseDto(10L, accountId,
                BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), LocalDateTime.now());

        when(balanceService.getBalance(accountId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId)
                        .header("x-user-id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceId").value(10L))
                .andExpect(jsonPath("$.accountId").value(accountId));
    }

    @Test
    void testUpdateBalanceWhenReturnUpdatedBalance() throws Exception {
        long accountId = 3L;
        BalanceOperationDto operationDto = new BalanceOperationDto();
        operationDto.setAmount(new BigDecimal("100.00"));
        operationDto.setOperationType(OperationType.DEPOSIT);

        BalanceResponseDto responseDto = new BalanceResponseDto(20L, accountId,
                new BigDecimal("200.00"), new BigDecimal("200.00"), LocalDateTime.now(), LocalDateTime.now());

        when(balanceService.updateBalance(accountId, operationDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/accounts/{accountId}/balance", accountId)
                        .header("x-user-id", "321")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceId").value(20L))
                .andExpect(jsonPath("$.actualBalance").value(200.00));
    }

    @Test
    void testCreateBalanceWhenInvalidAccountId() throws Exception {
        mockMvc.perform(post("/api/v1/accounts/{accountId}/balance", -1L)
                        .header("x-user-id", "123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateBalanceWhenInvalidOperationType() throws Exception {
        String invalidJson = """
                {
                    "accountId": 5,
                    "amount": 100.00,
                    "operationType": "INVALID_OP"
                }
                """;

        mockMvc.perform(put("/api/v1/accounts/{accountId}/balance", 5L)
                        .header("x-user-id", "321")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}