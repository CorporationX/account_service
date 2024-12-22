package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.entity.account.Type;
import faang.school.accountservice.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @Spy
    private ObjectMapper objectMapper;

    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        accountDto = new AccountDto();
    }

    @Test
    void testGetAccount() throws Exception {
        when(accountService.getAccount(1, 1))
                .thenReturn(List.of(new AccountDto(), new AccountDto()));

        mockMvc.perform(get("/api/v1/accounts/owners/1")
                        .param("ownerType", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    void testOpenNewAccount() throws Exception {
        CreateAccountDto createAccountDto = new CreateAccountDto();
        createAccountDto.setType(Type.FOREX_ACCOUNT);
        createAccountDto.setCurrency(Currency.EUR);
        createAccountDto.setOwnerType(OwnerType.USER);
        createAccountDto.setOwnerId(1);

        String json = objectMapper.writeValueAsString(createAccountDto);

        accountDto.setId(23L);

        when(accountService.openNewAccount(createAccountDto)).thenReturn(accountDto);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("23"));
    }

    @Test
    void testChangeStatus() throws Exception {
        accountDto.setStatus(Status.CLOSED);

        when(accountService.changeStatus(1, Status.CLOSED)).thenReturn(accountDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/1")
                        .param("status", "CLOSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }
}