package faang.school.accountservice.controller;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
public class BalanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private UserContext userContext;

    Long accountId = 1L;
    BigDecimal amount = new BigDecimal("1000.00");
    BigDecimal expectedBalance = new BigDecimal("1500.00");
    BalanceViewDto balanceViewDto = new BalanceViewDto();

    @Test
    @DisplayName("При валидных данных возвращает DTO")
    void authorize_Amount_WhenValidRequest_ReturnsDto() throws Exception {
        when(balanceService.authorizeAmount(accountId, amount)).thenReturn(balanceViewDto);

        mockMvc.perform(post("/balance/{accountId}/authorize", accountId)
                        .param("amount", amount.toString())
                        .header("x-user-id", 1))
                .andExpect(status().isOk());

        verify(balanceService).authorizeAmount(accountId, amount);
    }

    @Test
    @DisplayName("Пи успешном клиринге возвращает DTO")
    void clear_AuthorizedAmount_WhenValidRequest_ReturnsDto() throws Exception {
        when(balanceService.clearAuthorizedAmount(accountId, amount)).thenReturn(balanceViewDto);

        mockMvc.perform(post("/balance/{accountId}/clear", accountId)
                        .param("amount", amount.toString())
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Пополнение счета")
    void deposit_Amount_WithPositiveAmount_ReturnsUpdatedBalance() throws Exception {
        when(balanceService.depositAmount(accountId, amount)).thenReturn(balanceViewDto);

        mockMvc.perform(post("/balance/{accountId}/deposit", accountId)
                        .param("amount", amount.toString())
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Отменяет авторизацию")
    void cancelAuthorization_WhenValidRequest_ReturnsDto() throws Exception {
        when(balanceService.cancelAuthorization(accountId, amount)).thenReturn(balanceViewDto);

        mockMvc.perform(post("/balance/{accountId}/cancel-auth", accountId)
                        .param("amount", amount.toString())
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("При существующем счете возвращает баланс")
    void getAvailableBalance_WhenAccountExists_ReturnsBalance() throws Exception {
        when(balanceService.getAvailableBalance(accountId)).thenReturn(expectedBalance);

        mockMvc.perform(get("/balance/{accountId}/available-balance", accountId)
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }
}
