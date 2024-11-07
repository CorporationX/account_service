package faang.school.accountservice.controller;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.account_owner.AccountOwnerDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@Sql(value = "/db/account/test_accounts_schema.sql")
@ExtendWith(MockitoExtension.class)
public class AccountControllerIT extends BaseContextTest {

    @MockBean
    private UserServiceClient userServiceClient;

    @BeforeEach
    public void setUp() {
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L));
        when(userServiceClient.getUser(2L)).thenReturn(new UserDto(2L));
    }

    @Test
    public void testGetAccountByOwner() throws Exception {
        String result = mockMvc.perform(get("/api/v1/accounts/1").header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn().getResponse().getContentAsString();

        AccountDto accountDto = objectMapper.readValue(result, AccountDto.class);
        assertEquals(1L, accountDto.id());
        assertEquals(AccountStatus.ACTIVE, accountDto.status());
        assertEquals(AccountType.CURRENCY_ACCOUNT, accountDto.accountType());
        assertEquals("123456789012", accountDto.accountNumber());
        assertEquals(Currency.USD, accountDto.currency());
        verify(userServiceClient).getUser(1L);
    }

    @Test
    public void testGetAccountForbiddenAccess() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/1").header("x-user-id", 2))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User 2 has no appropriate privileges"));
        verify(userServiceClient).getUser(2L);
    }

    @Test
    public void testGetAccountsOk() throws Exception {
        mockMvc.perform(get("/api/v1/accounts?ownerId=1").header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    public void testGetAccountsWithoutOwnerIdParam() throws Exception {
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L));
        mockMvc.perform(get("/api/v1/accounts").header("x-user-id", 1))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userServiceClient);
    }

    @Test
    public void testGetAccountsWithFilters() throws Exception {
        mockMvc.perform(get("/api/v1/accounts?ownerId=1&currency=USD&status=ACTIVE").header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    public void testCreateAccountOk() throws Exception {
        AccountOwnerDto accountOwnerDto = AccountOwnerDto.builder()
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .build();
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .accountNumber("1234567891011")
                .accountType(AccountType.INDIVIDUAL_ACCOUNT)
                .owner(accountOwnerDto)
                .currency(Currency.RUB)
                .build();
        mockMvc.perform(post("/api/v1/accounts")
                        .header("x-user-id", 1)
                        .content(objectMapper.writeValueAsString(createAccountDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    public void testCreateAccountWithoutOwner() throws Exception {
        CreateAccountDto createAccountDto = CreateAccountDto.builder()
                .accountNumber("1234567891011")
                .accountType(AccountType.INDIVIDUAL_ACCOUNT)
                .currency(Currency.RUB)
                .build();
        mockMvc.perform(post("/api/v1/accounts")
                        .header("x-user-id", 1)
                        .content(objectMapper.writeValueAsString(createAccountDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations.[0].fieldName").value("owner"))
                .andExpect(jsonPath("$.violations.[0].message").value("must not be null"));
        verifyNoInteractions(userServiceClient);
    }

    @Test
    public void testUpdateAccountOk() throws Exception {
        UpdateAccountDto updateAccountDto = UpdateAccountDto.builder()
                .status(AccountStatus.FROZEN)
                .build();
        mockMvc.perform(put("/api/v1/accounts/3")
                        .header("x-user-id", 2)
                        .content(objectMapper.writeValueAsString(updateAccountDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FROZEN"));
        verify(userServiceClient).getUser(2L);
    }

    @Test
    public void testUpdateAccountWithSameStatus() throws Exception {
        UpdateAccountDto updateAccountDto = UpdateAccountDto.builder()
                .status(AccountStatus.ACTIVE)
                .build();
        mockMvc.perform(put("/api/v1/accounts/3")
                        .header("x-user-id", 2)
                        .content(objectMapper.writeValueAsString(updateAccountDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can't change account 3 with status ACTIVE to status ACTIVE"));
    }
}
