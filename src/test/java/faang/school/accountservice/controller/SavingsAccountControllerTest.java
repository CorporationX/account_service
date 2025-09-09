package faang.school.accountservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.SavingsAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = SavingsAccountController.class)
class SavingsAccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SavingsAccountService service;

    private static final UUID ACCOUNT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final long TARIFF_ID = 1L;
    private static final String TARIFF_TYPE = "BASE";
    private static final long USER_ID = 2;
    private static final long PROJECT_ID = 1;

    @Test
    @DisplayName("200 ОК - POST /v1/accounts/savings/{accountId}/tariffs/{tariffId}")
    void positive_shouldCallCreateSavings() throws Exception {
        SavingsAccountDto savingsDto = createSavingsAccountDto(USER_ID, null);
        when(service.create(ACCOUNT_ID, TARIFF_ID)).thenReturn(savingsDto);

        mockMvc.perform(post("/v1/accounts/savings/{accountId}/tariffs/{tariffId}", ACCOUNT_ID, TARIFF_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(savingsDto), true))
                .andExpect(jsonPath("$.account.id").value(ACCOUNT_ID.toString()))
                .andExpect(jsonPath("$.account.accountType").value(AccountType.SAVINGS.toString()));

        verify(service, times(1)).create(ACCOUNT_ID, TARIFF_ID);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/accounts/savings/{id}")
    void positive_shouldFindSavingsById() throws Exception {
        SavingsAccountDto savingsDto = createSavingsAccountDto(USER_ID, null);
        when(service.findById(ACCOUNT_ID)).thenReturn(savingsDto);

        mockMvc.perform(get("/v1/accounts/savings/{id}", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(savingsDto), true))
                .andExpect(jsonPath("$.account.id").value(ACCOUNT_ID.toString()));

        verify(service, times(1)).findById(ACCOUNT_ID);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/accounts/savings/users/{id}")
    void positive_shouldFindSavingsByUserId() throws Exception {
        SavingsAccountDto savingsDto = createSavingsAccountDto(USER_ID, null);
        when(service.findByUserId(USER_ID)).thenReturn(savingsDto);

        mockMvc.perform(get("/v1/accounts/savings/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(savingsDto), true))
                .andExpect(jsonPath("$.account.userId").value(USER_ID));

        verify(service, times(1)).findByUserId(USER_ID);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/accounts/savings/projects/{id}")
    void positive_shouldFindSavingsByProjectId() throws Exception {
        SavingsAccountDto savingsDto = createSavingsAccountDto(null, PROJECT_ID);
        when(service.findByProjectId(PROJECT_ID)).thenReturn(savingsDto);

        mockMvc.perform(get("/v1/accounts/savings/projects/{id}", PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(savingsDto), true))
                .andExpect(jsonPath("$.account.projectId").value(PROJECT_ID));

        verify(service, times(1)).findByProjectId(PROJECT_ID);
    }

    // ----------------------------

    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private SavingsAccountDto createSavingsAccountDto(Long userId, Long projectId) {
        return SavingsAccountDto.builder()
                .account(createAccountDto(userId, projectId))
                .currentTariff(TARIFF_TYPE)
                .currentRate(BigDecimal.TEN)
                .build();
    }

    private AccountDto createAccountDto(Long userId, Long projectId) {
        return AccountDto.builder()
                .id(ACCOUNT_ID)
                .userId(userId)
                .projectId(projectId)
                .accountType(AccountType.SAVINGS)
                .currency(Currency.RUB)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}