package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.AccountBalanceResponse;
import faang.school.accountservice.dto.account.AccountOpenRequest;
import faang.school.accountservice.dto.account.AccountResponse;
import faang.school.accountservice.dto.account.BalanceUpdateRequest;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.handler.GlobalExceptionHandler;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class AccountControllerTest {
    private static final String BASE_URL = "/api/v1/accounts";
    private static final String ACCOUNT_NUMBER = "42000000000000000001";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.01);
    private static final BalanceUpdateRequest BALANCE_UPDATE_REQUEST = new BalanceUpdateRequest(AMOUNT);
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AccountOpenRequest request;
    private AccountResponse accountResponse;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        request = new AccountOpenRequest(123L, OwnerType.USER, AccountType.PERSONAL, Currency.USD);
        accountResponse = new AccountResponse(1L, ACCOUNT_NUMBER, 123L, OwnerType.USER,
                AccountType.PERSONAL, Currency.USD, BigDecimal.valueOf(0.00), AccountStatus.ACTIVE);
    }

    @Test
    void testOpen() throws Exception {
        doNothing().when(accountService).open(request);

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
        verify(accountService, times(1)).open(request);
    }

    @Test
    void testGet() throws Exception {
        when(accountService.get(ACCOUNT_NUMBER)).thenReturn(accountResponse);

        mockMvc.perform(
                        get(BASE_URL + "/{accountNumber}", ACCOUNT_NUMBER)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.ownerId").value(123L))
                .andExpect(jsonPath("$.ownerType").value(OwnerType.USER.name()))
                .andExpect(jsonPath("$.accountType").value(AccountType.PERSONAL.name()))
                .andExpect(jsonPath("$.currency").value(Currency.USD.name()))
                .andExpect(jsonPath("$.balance").value(BigDecimal.valueOf(0.00)))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));

        verify(accountService, times(1)).get(ACCOUNT_NUMBER);

    }

    @Test
    void testGetByOwnerIdAndOwnerType() throws Exception {
        Long ownerId = 123L;
        OwnerType ownerType = OwnerType.USER;

        List<AccountResponse> responseList = List.of(accountResponse);
        when(accountService.get(ownerId, ownerType)).thenReturn(responseList);

        mockMvc.perform(
                        get(BASE_URL)
                                .param("ownerId", ownerId.toString())
                                .param("ownerType", ownerType.name())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber").value(accountResponse.accountNumber().toString()))
                .andExpect(jsonPath("$[0].ownerId").value(accountResponse.ownerId()))
                .andExpect(jsonPath("$[0].ownerType").value(accountResponse.ownerType().name()))
                .andExpect(jsonPath("$[0].accountType").value(accountResponse.accountType().name()))
                .andExpect(jsonPath("$[0].currency").value(accountResponse.currency().name()))
                .andExpect(jsonPath("$[0].balance").value(accountResponse.balance().toString()))
                .andExpect(jsonPath("$[0].status").value(accountResponse.status().name()));

        verify(accountService, times(1)).get(ownerId, ownerType);
    }


    @Test
    void testBlock() throws Exception {
        doNothing().when(accountService).block(ACCOUNT_NUMBER);

        mockMvc.perform(patch(BASE_URL + "/{accountNumber}/block", ACCOUNT_NUMBER))
                .andExpect(status().isNoContent());
        verify(accountService, times(1)).block(ACCOUNT_NUMBER);
    }

    @Test
    void testUnblock() throws Exception {
        doNothing().when(accountService).unblock(ACCOUNT_NUMBER);

        mockMvc.perform(patch(BASE_URL + "/{accountNumber}/unblock", ACCOUNT_NUMBER))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).unblock(ACCOUNT_NUMBER);
    }

    @Test
    void testClose() throws Exception {
        doNothing().when(accountService).close(ACCOUNT_NUMBER);

        mockMvc.perform(patch(BASE_URL + "/{accountNumber}/close", ACCOUNT_NUMBER))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).close(ACCOUNT_NUMBER);
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(accountService).delete(ACCOUNT_NUMBER);

        mockMvc.perform(patch(BASE_URL + "/{accountNumber}/delete", ACCOUNT_NUMBER))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).delete(ACCOUNT_NUMBER);

    }

    @Test
    void testUpdateBalance() throws Exception {
        AccountBalanceResponse response = new AccountBalanceResponse(AMOUNT, null, 1L);
        when(accountService.updateBalance(ACCOUNT_NUMBER, AMOUNT)).thenReturn(response);

        mockMvc.perform(
                        patch(BASE_URL + "/{accountNumber}/balance", ACCOUNT_NUMBER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(BALANCE_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(AMOUNT.toString()));
    }
}