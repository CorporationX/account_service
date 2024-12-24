package faang.school.accountservice.controller.balance;

import faang.school.accountservice.controller.advice.GlobalExceptionHandler;
import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {
    private static final Long VALID_ACCOUNT_ID = 1L;
    private static final Long INVALID_ACCOUNT_ID = 2L;
    private static final Long BALANCE_ID = 100L;
    private static final BigDecimal AUTH_BALANCE_INITIAL = new BigDecimal("500.00");
    private static final BigDecimal ACTUAL_BALANCE_INITIAL = new BigDecimal("1000.00");

    private MockMvc mockMvc;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    private ResponseBalanceDto validResponseBalanceDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        validResponseBalanceDto = ResponseBalanceDto.builder()
                .id(BALANCE_ID)
                .authorizationBalance(AUTH_BALANCE_INITIAL)
                .actualBalance(ACTUAL_BALANCE_INITIAL)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/account/{accountId}/balance/ - Success")
    public void getBalanceByAccountId_Success() throws Exception {
        when(balanceService.getBalance(VALID_ACCOUNT_ID)).thenReturn(validResponseBalanceDto);

        mockMvc.perform(get("/api/v1/account/{accountId}/balance/", VALID_ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BALANCE_ID))
                .andExpect(jsonPath("$.authorizationBalance").value(AUTH_BALANCE_INITIAL.doubleValue()))
                .andExpect(jsonPath("$.actualBalance").value(ACTUAL_BALANCE_INITIAL.doubleValue()));

        verify(balanceService).getBalance(VALID_ACCOUNT_ID);
    }

    @Test
    @DisplayName("GET /api/v1/account/{accountId}/balance/ - Account Not Found")
    public void getBalanceByAccountId_NotFound() throws Exception {
        when(balanceService.getBalance(INVALID_ACCOUNT_ID))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get("/api/v1/account/{accountId}/balance/", INVALID_ACCOUNT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Account not found"))
                .andExpect(jsonPath("$.instance").value("/api/v1/account/2/balance/"));

        verify(balanceService).getBalance(INVALID_ACCOUNT_ID);
    }
}
