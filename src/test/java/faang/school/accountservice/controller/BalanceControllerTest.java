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

    @BeforeEach
    public void setUp() {
        accountNumber = "01234567891231232";
        objectMapper = new ObjectMapper();
        balanceDto = BalanceDto.builder()
                .accountNumber(accountNumber)
                .authorizedBalance(new BigDecimal("1111.64"))
                .actualBalance(new BigDecimal("2222.23"))
                .build();
        amount = new BigDecimal("1000");
    }

    @Test
    @DisplayName("Authorize balance: success case")
    void testAuthorizeBalance_Success() throws Exception {
        when(balanceService.authorize(accountNumber)).thenReturn(balanceDto);

        mockMvc.perform(post("/accounts/balances")
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

        mockMvc.perform(post("/accounts/balances")
                        .param("accountNumber", accountNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be numeric positive value"));
    }

    @Test
    @DisplayName("Authorize balance: account number size mismatch")
    void testAuthorizeBalance_SizeMismatch() throws Exception {
        accountNumber = "1234";

        mockMvc.perform(post("/accounts/balances")
                        .param("accountNumber", accountNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber")
                        .value("Account number must be between 12 and 20 characters"));
    }

    @Test
    @DisplayName("Deposit authorized balance: success case")
    void testDepositAuthorizedBalance_Success() throws Exception {

        when(balanceService.depositAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/deposit")
                .param("accountNumber", accountNumber)
                .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountNumber").value("01234567891231232"))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Deposit authorized balance: negative balance value")
    void testDepositAuthorizedBalance_NegativeBalanceValue() throws Exception {
        accountNumber = "-1000000000000";

        when(balanceService.depositAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be numeric positive value"));
    }

    @Test
    @DisplayName("Deposit authorized balance: account number size mismatch")
    void testDepositAuthorizedBalance_SizeMismatch() throws Exception {
        accountNumber = "100000";

        when(balanceService.depositAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be between 12 and 20 characters"));
    }

    @Test
    @DisplayName("Deposit authorized balance: negative amount value")
    void testDepositAuthorizedBalance_NegativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.depositAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }

    @Test
    @DisplayName("Withdraw authorized balance: success case")
    void testWithdrawAuthorizedBalance_Success() throws Exception {

        when(balanceService.withdrawAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/withdraw")
                        .param("accountNumber", String.valueOf(accountNumber))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountNumber").value("01234567891231232"))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Withdraw authorized balance: negative account number")
    void testWithdrawAuthorizedBalance_NegativeAccountNumber() throws Exception {
        accountNumber = "-1000000000000";

        when(balanceService.withdrawAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be numeric positive value"));
    }

    @Test
    @DisplayName("Withdraw authorized balance: account number size mismatch")
    void testWithdrawAuthorizedBalance_SizeMismatch() throws Exception {
        accountNumber = "10000";

        when(balanceService.withdrawAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be between 12 and 20 characters"));
    }

    @Test
    @DisplayName("Deposit authorized balance: negative amount value")
    void testWithdrawAuthorizedBalance_NegativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.withdrawAuthorized(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/authorized/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }

    @Test
    @DisplayName("Deposit actual balance: success case")
    void testDepositActualBalance_Success() throws Exception {

        when(balanceService.depositActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountNumber").value("01234567891231232"))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Deposit actual balance: negative account number")
    void testDepositActualBalance_NegativeAccountNumber() throws Exception {
        accountNumber = "-100000000000";

        when(balanceService.depositActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be numeric positive value"));
    }


    @Test
    @DisplayName("Deposit actual balance: account number size mismatch")
    void testDepositActualBalance_SizeMismatch() throws Exception {
        accountNumber = "1000";

        when(balanceService.depositActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be between 12 and 20 characters"));
    }

    @Test
    @DisplayName("Deposit actual balance: negative amount value")
    void testDepositActualBalance_NegativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.depositActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/deposit")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }

    @Test
    @DisplayName("Withdraw actual balance: success case")
    void testWithdrawActualBalance_Success() throws Exception {

        when(balanceService.withdrawActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(jsonPath("$.accountNumber").value("01234567891231232"))
                .andExpect(jsonPath("$.authorizedBalance").value(1111.64))
                .andExpect(jsonPath("$.actualBalance").value(2222.23));
    }

    @Test
    @DisplayName("Withdraw actual balance: negative account value")
    void testWithdrawActualBalance_NegativeAccountValue() throws Exception {
        accountNumber = "-100000000000";

        when(balanceService.withdrawActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be numeric positive value"));
    }

    @Test
    @DisplayName("Withdraw actual balance: account number size mismatch")
    void testWithdrawActualBalance_SizeMismatch() throws Exception {
        accountNumber = "100000";

        when(balanceService.withdrawActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be between 12 and 20 characters"));
    }

    @Test
    @DisplayName("Withdraw actual balance: negative amount value")
    void testWithdrawActualBalance_NegativeAmountValue() throws Exception {
        amount = BigDecimal.valueOf(-0.01);

        when(balanceService.withdrawActual(accountNumber, amount)).thenReturn(balanceDto);

        mockMvc.perform(put("/accounts/balances/actual/withdraw")
                        .param("accountNumber", accountNumber)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be positive value"));
    }
}
