package faang.school.accountservice.balance;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.controller.BalanceController;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.exception.AccessException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.NotEnoughFundsException;
import faang.school.accountservice.exception.SelfPayException;
import faang.school.accountservice.exception.WrongAmountException;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BalanceController.class)
public class ControllerTest {
    private static final String BALANCE_URL = "/balances/{balanceId}";
    private static final String DEPOSIT_URL = "/balances/{balanceId}/deposit/{amount}";
    private static final String WITHDRAW_URL = "/balances/{balanceId}/withdraw/{amount}";
    private static final String SEND_URL = "/balances/{senderId}/send/{receiverId}/{amount}";
    private static final String GET_TRANSACTIONS_URL = "/balances/{balanceId}/transactions";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private UserContext context;

    @Test
    public void positiveGetBalance() throws Exception {
        Long balanceId = 1L;
        Long userId = 1L;
        BalanceResponseDto mockResponse = new BalanceResponseDto(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(800)
        );

        when(balanceService.getBalance(balanceId)).thenReturn(mockResponse);

        mockMvc.perform(get(BALANCE_URL, balanceId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBalance").value(1000))
                .andExpect(jsonPath("$.availableBalance").value(800));

    }

    @Test
    void negativeBalanceNotFound() throws Exception {
        Long nonExistentBalanceId = 999L;
        Long userId = 1L;

        when(context.getUserId()).thenReturn(userId);
        when(balanceService.getBalance(nonExistentBalanceId))
                .thenThrow(new BalanceNotFoundException("Баланс не найден"));

        mockMvc.perform(get(BALANCE_URL, nonExistentBalanceId)
                        .header("x-user-id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Ошибка").value("Баланс не найден"));
    }

    @Test
    void negativeGetBalanceAccountNotFound() throws Exception {
        when(context.getUserId()).thenReturn(1L);
        when(balanceService.getBalance(anyLong())).thenThrow(
                new IllegalArgumentException("Account или UserContext не инициализированы")
        );

        mockMvc.perform(get(BALANCE_URL, 1L)
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Ошибка").value("Account или UserContext не инициализированы"));
    }

    @Test
    void negativeGetBalanceNotOwner() throws Exception {
        when(context.getUserId()).thenReturn(2L);
        when(balanceService.getBalance(anyLong())).thenThrow(
                new AccessException("Нет доступа к этому аккаунту")
        );

        mockMvc.perform(get(BALANCE_URL, 1L)
                        .header("x-user-id", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.Ошибка").value("Доступ запрещен"));
    }

    @Test
    void positiveDepositFunds() throws Exception {
        Long balanceId = 1L;
        BigDecimal amount = new BigDecimal("500");
        String comment = "test";
        Long userId = 2L;

        when(context.getUserId()).thenReturn(userId);
        doNothing().when(balanceService).addFunds(any(), any(), any());

        mockMvc.perform(post(DEPOSIT_URL, balanceId, amount)
                        .param("comment", comment)
                        .header("x-user-id", userId.toString()))
                .andExpect(status().isOk());

        verify(balanceService).addFunds(eq(balanceId), eq(amount), eq(comment));
    }

    @Test
    void negativeDepositFundsAccountNotFound() throws Exception {
        BigDecimal amount = new BigDecimal("500");
        doThrow(new IllegalArgumentException("Account или UserContext не инициализированы"))
                .when(balanceService)
                .addFunds(eq(1L), eq(amount), eq("Deposit"));


        mockMvc.perform(post(DEPOSIT_URL, 1L, amount)
                        .param("comment", "Deposit")
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }


    @Test
    void negativeDepositFundsUserNotHaveAccess() throws Exception {
        BigDecimal amount = new BigDecimal("500");
        doThrow(new AccessException("Доступ запрещен"))
                .when(balanceService)
                .addFunds(eq(1L), eq(amount), eq("Deposit"));


        mockMvc.perform(post(DEPOSIT_URL, 1L, amount)
                        .param("comment", "Deposit")
                        .header("x-user-id", 500L))
                .andExpect(status().isForbidden());
    }

    @Test
    void positiveWithdrawFunds() throws Exception {
        Long balanceId = 1L;
        BigDecimal amount = new BigDecimal("500");
        String comment = "Withdraw";
        Long userId = 2L;

        when(context.getUserId()).thenReturn(userId);
        doNothing().when(balanceService).withdrawFunds(any(), any(), any());

        mockMvc.perform(post(WITHDRAW_URL, balanceId, amount)
                        .param("comment", comment)
                        .header("x-user-id", userId.toString()))
                .andExpect(status().isOk());

        verify(balanceService).withdrawFunds(eq(balanceId), eq(amount), eq(comment));
    }

    @Test
    void negativeWithdrawFundsBalanceNotFound() throws Exception {
        BigDecimal amount = new BigDecimal("500");

        doThrow(new BalanceNotFoundException("Баланс не найден"))
                .when(balanceService)
                .withdrawFunds(eq(1L), eq(amount), eq("Withdraw"));

        mockMvc.perform(post(WITHDRAW_URL, 1L, amount)
                        .param("comment", "Withdraw")
                        .header("x-user-id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void negativeWithdrawFundsAccountNotFound() throws Exception {
        BigDecimal amount = new BigDecimal("500");
        doThrow(new IllegalArgumentException("Account или UserContext не инициализированы"))
                .when(balanceService)
                .withdrawFunds(eq(1L), eq(amount), eq("Withdraw"));


        mockMvc.perform(post(WITHDRAW_URL, 1L, amount)
                        .param("comment", "Withdraw")
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }


    @Test
    void negativeWithdrawFundsAccessDenied() throws Exception {
        BigDecimal amount = new BigDecimal("500");
        doThrow(new AccessException("Доступ запрещен"))
                .when(balanceService)
                .withdrawFunds(eq(1L), eq(amount), eq("Withdraw"));


        mockMvc.perform(post(WITHDRAW_URL, 1L, amount)
                        .param("comment", "Withdraw")
                        .header("x-user-id", 500L))
                .andExpect(status().isForbidden());
    }

    @Test
    void positiveSendPayment() throws Exception {
        BigDecimal amount = new BigDecimal("100");

        when(context.getUserId()).thenReturn(1L);

        doNothing().when(balanceService)
                .sendPayment(eq(1L), eq(2L), eq(amount), anyString());

        mockMvc.perform(post(SEND_URL, 1L, 2L, amount)
                        .param("comment", "")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk());

        verify(balanceService, times(1))
                .sendPayment(eq(1L), eq(2L), eq(amount), anyString());
    }

    @Test
    void negativeSendPaymentBalanceNotFound() throws Exception {
        BigDecimal amount = new BigDecimal("500");

        doThrow(new BalanceNotFoundException("Баланс не найден"))
                .when(balanceService)
                .sendPayment(eq(1L), eq(2L), eq(amount), eq("Send"));

        mockMvc.perform(post(SEND_URL, 1L, 2L, amount)
                        .param("comment", "Send")
                        .header("x-user-id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void negativeSendPaymentAccountNotFound() throws Exception {
        BigDecimal amount = new BigDecimal("500");

        doThrow(new IllegalArgumentException("Account или UserContext не инициализированы"))
                .when(balanceService)
                .sendPayment(eq(1L), eq(2L), eq(amount), eq("Send"));

        mockMvc.perform(post(SEND_URL, 1L, 2L, amount)
                        .param("comment", "Send")
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeSendPaymentAccessDenied() throws Exception {
        BigDecimal amount = new BigDecimal("500");

        when(context.getUserId()).thenReturn(30L);

        doThrow(new AccessException("Доступ запрещен"))
                .when(balanceService)
                .sendPayment(eq(1L), eq(2L), eq(amount), eq("Send"));

        mockMvc.perform(post(SEND_URL, 1L, 2L, amount)
                        .param("comment", "Send")
                        .header("x-user-id", 30L))
                .andExpect(status().isForbidden());
    }

    @Test
    void negativeSendPaymentNotEnoughFunds() throws Exception {
        BigDecimal amount = new BigDecimal("500");

        doThrow(new NotEnoughFundsException("Недостаточно средств"))
                .when(balanceService)
                .sendPayment(eq(1L), eq(2L), eq(amount), eq("Send"));

        mockMvc.perform(post(SEND_URL, 1L, 2L, amount)
                        .param("comment", "Send")
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeSendPaymentWrongAmount() throws Exception {
        BigDecimal amount = new BigDecimal("-500");

        doThrow(new WrongAmountException("Неверная сумма"))
                .when(balanceService)
                .sendPayment(eq(1L), eq(2L), eq(amount), eq("Send"));

        mockMvc.perform(post(SEND_URL, 1L, 2L, amount)
                        .param("comment", "Send")
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeSendPaymentSelfSend() throws Exception {
        BigDecimal amount = new BigDecimal("500");

        doThrow(new SelfPayException("Недопустимый платеж"))
                .when(balanceService)
                .sendPayment(eq(1L), eq(1L), eq(amount), eq("Send"));

        mockMvc.perform(post(SEND_URL, 1L, 1L, amount)
                        .param("comment", "Send")
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void positiveGetTransactions() throws Exception {
        TransactionDto first = TransactionDto.builder().build();
        TransactionDto second = TransactionDto.builder().build();

        when(balanceService.getTransactions(1L)).thenReturn(List.of(first, second));

        mockMvc.perform(get(GET_TRANSACTIONS_URL, 1L)
                .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
