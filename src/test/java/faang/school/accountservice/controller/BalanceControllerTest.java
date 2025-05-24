package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.handler.GlobalExceptionHandler;
import faang.school.accountservice.service.BalanceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BalanceController.class)
@ContextConfiguration(classes = {BalanceController.class, GlobalExceptionHandler.class})
class BalanceControllerTest {

    @MockBean
    private BalanceService balanceService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long accountId = 1L;
    BalanceDto balanceDto;

    @BeforeEach
    void setUp() {
        balanceDto = BalanceDto.builder()
                .accountId(accountId)
                .actualBalance(new BigDecimal("100"))
                .authorizedBalance(new BigDecimal("50"))
                .build();
    }

    @Nested
    class getBalance {
        @Test
        public void success() throws Exception {
            when(balanceService.getByAccountId(accountId)).thenReturn(balanceDto);

            mockMvc.perform(get("/balances/{accountId}", accountId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId))
                    .andExpect(jsonPath("$.actualBalance").value(100.00))
                    .andExpect(jsonPath("$.authorizedBalance").value(50.00));

            verify(balanceService, times(1)).getByAccountId(accountId);
        }

        @Test
        public void entityNotFoundException() throws Exception {
            when(balanceService.getByAccountId(accountId))
                    .thenThrow(new EntityNotFoundException("Balance not found for account ID: " + accountId));

            mockMvc.perform(get("/balances/{accountId}", accountId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Balance not found for account ID: " + accountId))
                    .andExpect(jsonPath("$.url").value("/balances/" + accountId));

            verify(balanceService, times(1)).getByAccountId(accountId);
        }
    }


    @Nested
    class createBalance {
        @Test
        public void success() throws Exception {
            when(balanceService.create(accountId)).thenReturn(balanceDto);

            mockMvc.perform(post("/balances/{accountId}", accountId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId))
                    .andExpect(jsonPath("$.actualBalance").value(100.00))
                    .andExpect(jsonPath("$.authorizedBalance").value(50.00));

            verify(balanceService, times(1)).create(accountId);
        }

        @Test
        public void entityNotFoundException() throws Exception {
            when(balanceService.create(accountId))
                    .thenThrow(new EntityNotFoundException("Account not found: " + accountId));

            mockMvc.perform(post("/balances/{accountId}", accountId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Account not found: " + accountId))
                    .andExpect(jsonPath("$.url").value("/balances/" + accountId));

            verify(balanceService, times(1)).create(accountId);
        }

        @Test
        public void illegalStateException() throws Exception {
            when(balanceService.create(accountId))
                    .thenThrow(new IllegalStateException("Balance already exists for account ID: {}" + accountId));

            mockMvc.perform(post("/balances/{accountId}", accountId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Balance already exists for account ID: {}" + accountId))
                    .andExpect(jsonPath("$.url").value("/balances/" + accountId));

            verify(balanceService, times(1)).create(accountId);
        }
    }

    @Nested
    class updateBalance {
        @Test
        void success() throws Exception {
            when(balanceService.updateBalanceSafely(eq(accountId), any(BalanceDto.class))).thenReturn(balanceDto);

            mockMvc.perform(put("/balances/{accountId}", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(balanceDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId))
                    .andExpect(jsonPath("$.actualBalance").value(100.00))
                    .andExpect(jsonPath("$.authorizedBalance").value(50.00));

            verify(balanceService, times(1)).updateBalanceSafely(eq(accountId), any(BalanceDto.class));
        }
    }
}