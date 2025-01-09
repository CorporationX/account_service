package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.GlobalExceptionHandler;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Balance controller tests")
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceController balanceController;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private UserContext userContext;

    private ObjectMapper objectMapper;

    private String accountNumber;

    private BalanceDto balanceDto;

    private BigDecimal amount;

    private Long balanceId;

    @BeforeEach
    public void setUp() {
        balanceId = 1L;
        accountNumber = "01234567891231232";
        objectMapper = new ObjectMapper();
        balanceDto = BalanceDto.builder()
                .accountId(2L)
                .authorizedBalance(new BigDecimal("1111.64"))
                .actualBalance(new BigDecimal("2222.23"))
                .build();
        amount = new BigDecimal("1000");
    }

    @Test
    @DisplayName("Authorize balance: success case")
    void testAuthorizeBalance_Success() throws Exception {
        when(balanceService.authorize(accountNumber)).thenReturn(balanceDto);

        mockMvc.perform(post("/balances")
                        .param("accountNumber", accountNumber))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.authorizedBalance").value("1111.64"))
                .andExpect(jsonPath("$.actualBalance").value("2222.23"));
    }

    @Test
    @DisplayName("Authorize balance: not numeric account number")
    void testAuthorizeBalance_TextAccountNumber() throws Exception {
        accountNumber = "TEXT-NUMBER1234";

        mockMvc.perform(post("/balances")
                        .param("accountNumber", accountNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be numeric"));
    }

    @Test
    @DisplayName("Authorize balance: account number size mismatch")
    void testAuthorizeBalance_SizeMismatch() throws Exception {
        accountNumber = "1234";

        mockMvc.perform(post("/balances")
                        .param("accountNumber", accountNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber")
                        .value("Account number must be between 12 and 20 characters"));
    }

    @Test
    @DisplayName("Deposit authorized balance: success case")
    void testDepositAuthorizedBalance_Success() throws Exception {

        when(balanceService.depositAuthorized(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/authorized/deposit")
                .param("balanceId", String.valueOf(balanceId))
                .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountId").value(2L))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Deposit authorized balance: negative balance value")
    void testDepositAuthorizedBalance_NegativeBalanceValue() throws Exception {
        balanceId = -1L;

        when(balanceService.depositAuthorized(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/authorized/deposit")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.balanceId").value("Balance ID must be positive value"));
    }

    @Test
    @DisplayName("Deposit authorized balance: negative amount value")
    void testDepositAuthorizedBalance_NegativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.depositAuthorized(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/authorized/deposit")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }

    @Test
    @DisplayName("Withdraw authorized balance: success case")
    void testWithdrawAuthorizedBalance_Success() throws Exception {

        when(balanceService.withdrawAuthorized(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/authorized/withdraw")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountId").value(2L))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Withdraw authorized balance - negative balance value")
    void testWithdrawAuthorizedBalance_negativeBalanceValue() throws Exception {
        balanceId = -1L;

        when(balanceService.withdrawAuthorized(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/authorized/withdraw")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.balanceId").value("Balance ID must be positive value"));
    }

    @Test
    @DisplayName("Deposit authorized balance - success case")
    void testWithdrawAuthorizedBalance_negativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.withdrawAuthorized(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/authorized/withdraw")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }

    @Test
    @DisplayName("Deposit actual balance: success case")
    void testDepositActualBalance_Success() throws Exception {

        when(balanceService.depositActual(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/actual/deposit")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountId").value(2L))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Deposit actual balance - negative balance value")
    void testDepositActualBalance_negativeBalanceValue() throws Exception {
        balanceId = -1L;

        when(balanceService.depositActual(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/actual/deposit")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.balanceId").value("Balance ID must be positive value"));
    }

    @Test
    @DisplayName("Deposit actual balance - success case")
    void testDepositActualBalance_negativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.depositActual(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/actual/deposit")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }

    @Test
    @DisplayName("Withdraw actual balance: success case")
    void testWithdrawActualBalance_Success() throws Exception {

        when(balanceService.withdrawActual(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/actual/withdraw")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountId").value(2L))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Withdraw actual balance - negative balance value")
    void testWithdrawActualBalance_negativeBalanceValue() throws Exception {
        balanceId = -1L;

        when(balanceService.withdrawActual(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/actual/withdraw")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.balanceId").value("Balance ID must be positive value"));
    }

    @Test
    @DisplayName("Withdraw actual balance - success case")
    void testWithdrawActualBalance_negativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.withdrawActual(balanceId, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/balances/actual/withdraw")
                        .param("balanceId", String.valueOf(balanceId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }
}
