package faang.school.accountservice.controller.account;

import com.google.gson.Gson;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.controller.account.AccountController;
import faang.school.accountservice.controller.advice.GlobalExceptionHandler;
import faang.school.accountservice.dto.account.RequestAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    private final long USER_ID = 25L;
    private final long ACCOUNT_ID = 17L;
    private final long INVALID_ACCOUNT_ID = 18L;

    private MockMvc mockMvc;

    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountService accountService;
    @Mock
    private UserContext userContext;

    private ResponseAccountDto responseAccountDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        responseAccountDto = ResponseAccountDto.builder()
                .number("40817810099910004312")
                .accountType(AccountType.FL)
                .currency(Currency.RUB)
                .status(AccountStatus.OPEN)
                .build();
    }

    @Test
    void testGetAccountWithIdWithException() throws Exception {
        when(accountService.getAccountWithId(INVALID_ACCOUNT_ID,USER_ID))
                .thenThrow(new AccountNotFoundException("Account not found"));
        when(userContext.getUserId()).thenReturn(USER_ID);

        mockMvc.perform(get("/api/v1/accounts/" + INVALID_ACCOUNT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Account not found"))
                .andExpect(jsonPath("$.instance").value("/api/v1/accounts/18"));

        verify(accountService).getAccountWithId(INVALID_ACCOUNT_ID,USER_ID);
    }

    @Test
    void testGetAccountWithId() throws Exception {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(accountService.getAccountWithId(ACCOUNT_ID,USER_ID)).thenReturn(responseAccountDto);

        mockMvc.perform(get("/api/v1/accounts/" + ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(responseAccountDto.getNumber()));
    }

    @Test
    void testGetAccountWithNumber() throws Exception {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(accountService.getAccountWithNumber("40817810099910004312",USER_ID)).thenReturn(responseAccountDto);

        mockMvc.perform(get("/api/v1/accounts/number/" + "40817810099910004312"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(responseAccountDto.getNumber()));
    }

    @Test
    void testBlockAccount() throws Exception {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(accountService.blockAccount(ACCOUNT_ID,USER_ID)).thenReturn(responseAccountDto);
        mockMvc.perform(put("/api/v1/accounts/" + ACCOUNT_ID + "/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(responseAccountDto.getNumber()));
    }

    @Test
    void testCloseAccount() throws Exception {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(accountService.closeAccount(ACCOUNT_ID,USER_ID)).thenReturn(responseAccountDto);
        mockMvc.perform(put("/api/v1/accounts/" + ACCOUNT_ID + "/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(responseAccountDto.getNumber()));
    }

    @Test
    void testUnblockAccount() throws Exception {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(accountService.unblockAccount(ACCOUNT_ID,USER_ID)).thenReturn(responseAccountDto);
        mockMvc.perform(put("/api/v1/accounts/" + ACCOUNT_ID + "/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(responseAccountDto.getNumber()));
    }
}
