package faang.school.accountservice.account;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.controller.AccountController;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.enums.AccountStatus;
import faang.school.accountservice.entity.account.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAccount() throws Exception {
        Long accountId = 1L;
        AccountDto accountDto = getAccountDto(AccountStatus.ACTIVE);

        when(accountService.get(accountId)).thenReturn(accountDto);

        mockMvc.perform(get("/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNumber").value("123456789012"))
                .andExpect(jsonPath("$.type").value(AccountType.CURRENCY_ACCOUNT.name()))
                .andExpect(jsonPath("$.currency").value(Currency.RUB.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));

        verify(accountService).get(accountId);
    }

    @Test
    void testOpenAccount() throws Exception {
        AccountDto accountDto = getAccountDto(AccountStatus.ACTIVE);

        when(accountService.openAccount(accountDto)).thenReturn(accountDto);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNumber").value("123456789012"))
                .andExpect(jsonPath("$.type").value(AccountType.CURRENCY_ACCOUNT.name()))
                .andExpect(jsonPath("$.currency").value(Currency.RUB.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));

        verify(accountService).openAccount(accountDto);
    }

    @Test
    void testFreezeAccount() throws Exception {
        Long accountId = 1L;
        AccountDto accountDto = getAccountDto(AccountStatus.FROZEN);

        when(accountService.freezeAccount(accountId)).thenReturn(accountDto);

        mockMvc.perform(post("/accounts/{id}/block", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNumber").value("123456789012"))
                .andExpect(jsonPath("$.type").value(AccountType.CURRENCY_ACCOUNT.name()))
                .andExpect(jsonPath("$.currency").value(Currency.RUB.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.FROZEN.name()));

        verify(accountService).freezeAccount(accountId);
    }

    @Test
    void testCloseAccount() throws Exception {
        Long accountId = 1L;
        AccountDto accountDto = getAccountDto(AccountStatus.CLOSED);

        when(accountService.closeAccount(accountId)).thenReturn(accountDto);

        mockMvc.perform(post("/accounts/{id}/close", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNumber").value("123456789012"))
                .andExpect(jsonPath("$.type").value(AccountType.CURRENCY_ACCOUNT.name()))
                .andExpect(jsonPath("$.currency").value(Currency.RUB.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.CLOSED.name()));

        verify(accountService).closeAccount(accountId);
    }

    @Test
    void testGetAllOfUser() throws Exception {
        Long userId = 1L;
        List<AccountDto> accounts = List.of(getAccountDto(AccountStatus.ACTIVE));

        when(accountService.getAllOfUser(userId)).thenReturn(accounts);

        mockMvc.perform(get("/accounts/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].paymentNumber").value("123456789012"))
                .andExpect(jsonPath("$[0].type").value(AccountType.CURRENCY_ACCOUNT.name()))
                .andExpect(jsonPath("$[0].currency").value(Currency.RUB.name()))
                .andExpect(jsonPath("$[0].status").value(AccountStatus.ACTIVE.name()));

        verify(accountService).getAllOfUser(userId);
    }

    @Test
    void testGetAllOfProject() throws Exception {
        Long projectId = 1L;
        List<AccountDto> accounts = List.of(getAccountDto(AccountStatus.ACTIVE));

        when(accountService.getAllOfProject(projectId)).thenReturn(accounts);

        mockMvc.perform(get("/accounts/project/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].paymentNumber").value("123456789012"))
                .andExpect(jsonPath("$[0].type").value(AccountType.CURRENCY_ACCOUNT.name()))
                .andExpect(jsonPath("$[0].currency").value(Currency.RUB.name()))
                .andExpect(jsonPath("$[0].status").value(AccountStatus.ACTIVE.name()));

        verify(accountService).getAllOfProject(projectId);
    }

    public AccountDto getAccountDto(AccountStatus status) {
        return AccountDto.builder()
                .paymentNumber("123456789012")
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.RUB)
                .status(status)
                .build();
    }
}
