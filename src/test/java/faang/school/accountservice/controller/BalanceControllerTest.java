package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.utilities.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {
    private final static String mainUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.BALANCE;
    private MockMvc mockMvc;
    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    private final long balanceId = 1L;
    private final long accountId = 2L;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
    }

    @Test
    public void getBalanceTest() throws Exception {
        BalanceDto balanceDto = getBalanceDto();

        when(balanceService.getBalance(accountId)).thenReturn(balanceDto);

        mockMvc.perform(get(mainUrl + "/" + accountId)
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balanceDto.getId()))
                .andExpect(jsonPath("$.actualBalance").value(BigDecimal.valueOf(800)))
                .andExpect(jsonPath("$.authorizationBalance").value(BigDecimal.valueOf(200)))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.accountId").value(accountId));
    }

    @Test
    public void createBalanceSuccessTest() throws Exception {
        BalanceDto balanceDto = getBalanceDto();

        when(balanceService.createBalance(accountId)).thenReturn(balanceDto);

        mockMvc.perform(post(mainUrl + "/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balanceId))
                .andExpect(jsonPath("$.actualBalance").value(BigDecimal.valueOf(800)))
                .andExpect(jsonPath("$.authorizationBalance").value(BigDecimal.valueOf(200)))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.accountId").value(accountId));

        verify(balanceService, times(1)).createBalance(accountId);
    }

    @Test
    public void authorizationBalanceSuccessTest() throws Exception {
        BalanceDto balanceDto = getBalanceDto();

        when(balanceService.authorizationBalance(anyLong(), any(BigDecimal.class))).thenReturn(balanceDto);

        mockMvc.perform(put(mainUrl + "/" + accountId + UrlUtils.AUTHORIZATION)
                        .param("amount", String.valueOf(BigDecimal.valueOf(100))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balanceId))
                .andExpect(jsonPath("$.actualBalance").value(BigDecimal.valueOf(800)))
                .andExpect(jsonPath("$.authorizationBalance").value(BigDecimal.valueOf(200)))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.accountId").value(accountId));

        verify(balanceService, times(1)).authorizationBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    public void updateAuthorizationBalanceSuccessTest() throws Exception {
        BalanceDto balanceDto = getBalanceDto();

        when(balanceService.updateAuthorizationBalance(anyLong(), any(BigDecimal.class))).thenReturn(balanceDto);

        mockMvc.perform(put(mainUrl + "/" + accountId + UrlUtils.AUTHORIZATION_BALANCE)
                        .param("amount", String.valueOf(BigDecimal.valueOf(100))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balanceId))
                .andExpect(jsonPath("$.actualBalance").value(BigDecimal.valueOf(800)))
                .andExpect(jsonPath("$.authorizationBalance").value(BigDecimal.valueOf(200)))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.accountId").value(accountId));

        verify(balanceService, times(1)).updateAuthorizationBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    public void updateActualBalanceSuccessTest() throws Exception {
        BalanceDto balanceDto = getBalanceDto();

        when(balanceService.updateActualBalance(anyLong(), any(BigDecimal.class))).thenReturn(balanceDto);

        mockMvc.perform(put(mainUrl + "/" + accountId + UrlUtils.ACTUAL_BALANCE)
                        .param("amount", String.valueOf(BigDecimal.valueOf(100))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balanceId))
                .andExpect(jsonPath("$.actualBalance").value(BigDecimal.valueOf(800)))
                .andExpect(jsonPath("$.authorizationBalance").value(BigDecimal.valueOf(200)))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.accountId").value(accountId));

        verify(balanceService, times(1)).updateActualBalance(anyLong(), any(BigDecimal.class));
    }

    private BalanceDto getBalanceDto() {
        LocalDateTime now = LocalDateTime.now();

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(balanceId);
        balanceDto.setActualBalance(BigDecimal.valueOf(800));
        balanceDto.setAuthorizationBalance(BigDecimal.valueOf(200));
        balanceDto.setCreatedAt(now);
        balanceDto.setUpdatedAt(now);
        balanceDto.setVersion(1);
        balanceDto.setAccountId(accountId);

        return balanceDto;
    }
}
