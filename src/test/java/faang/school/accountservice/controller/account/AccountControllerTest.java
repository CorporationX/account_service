package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.controller.AccountController;
import faang.school.accountservice.dto.account.AccountReq;
import faang.school.accountservice.dto.account.AccountResp;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.utilities.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    private MockMvc mockMvc;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private AccountController accountController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void getAccountSuccessTest() throws Exception {
        AccountResp accountResp = AccountResp.builder().id(1L).accountNumber("123456789123").build();
        when(accountService.getAccount(1L)).thenReturn(accountResp);
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.ACCOUNTS + "/1")
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountResp.getId()))
                .andExpect(jsonPath("$.accountNumber").value(accountResp.getAccountNumber()));
    }

    @Test
    void openAccountWithUserOwnerSuccessTest() throws Exception {
        AccountReq accountReq = AccountReq.builder()
                .accountNumber("111111111111")
                .projectOwnerId(1L)
                .currency(Currency.RUB.name())
                .accountType(AccountType.CURRENT.name())
                .build();
        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.ACCOUNTS)
                        .param("isProjectAccount", "false")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountReq))
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void blockAccountSuccessTest() throws Exception {
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.ACCOUNTS + "/1" + UrlUtils.BLOCK)
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void closeAccountSuccessTest() throws Exception {
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.ACCOUNTS + "/1" + UrlUtils.CLOSE)
                        .header("x-user-id", 1))
                .andExpect(status().isOk());
    }
}
