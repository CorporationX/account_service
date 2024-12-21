package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.GlobalExceptionHandler;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountController accountController;

    @MockBean
    private UserContext userContext;

    @MockBean
    private AccountService accountService;

    private ObjectMapper objectMapper;
    private CreateAccountDto createDto;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Open new account success: valid input")
    void testCreateAccount_Success() throws Exception {
        String jsonCreateDto = "{\"ownerType\": \"project\", \"ownerName\": \"Project owner\", \"accountType\": \"Current\", \"currency\": \"usd\"}";
        createDto = objectMapper.readValue(jsonCreateDto, CreateAccountDto.class);
        accountDto = new AccountDto(1L, "ACC123456", AccountOwnerType.PROJECT, 10L, "Project owner", AccountType.CURRENT, Currency.USD, AccountStatus.ACTIVE);

        when(userContext.getUserId()).thenReturn(10L);
        when(accountService.createAccount(createDto, 10L)).thenReturn(accountDto);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10")
                        .content(jsonCreateDto))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(accountDto)))
                .andExpect(jsonPath("$.accountNumber").value("ACC123456"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Open new account fail: blank owner name")
    void testCreateAccount_BlankOwnerName_Fail() throws Exception {
        String jsonCreateDto = "{\"ownerType\": \"project\", \"ownerName\": \"\", \"accountType\": \"Current\", \"currency\": \"rub\"}";

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10")
                        .content(jsonCreateDto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ownerName").value("Owner name cannot be empty"));
    }

    @Test
    @DisplayName("Open new account fail: invalid account type")
    void testCreateAccount_InvalidAccountType_Fail() throws Exception {
        String jsonCreateDto = "{\"ownerType\": \"project\", \"ownerName\": \"project 1\", \"accountType\": \"X\", \"currency\": \"rub\"}";

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10")
                        .content(jsonCreateDto))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid account type")));
    }

    @Test
    @DisplayName("Get accounts information success: valid input")
    void testGetAccounts_Success() throws Exception {
        String accountOwnerType = "project";
        accountDto = new AccountDto(1L, "ACC123456", AccountOwnerType.PROJECT, 10L, "Project owner", AccountType.CURRENT, Currency.USD, AccountStatus.ACTIVE);
        AccountOwnerType ownerType = AccountOwnerType.toValue(accountOwnerType);

        when(userContext.getUserId()).thenReturn(10L);
        when(accountService.getAccounts(ownerType, 10L)).thenReturn(List.of(accountDto));

        mockMvc.perform(get("/accounts")
                        .param("accountOwnerType", accountOwnerType)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(accountDto))))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Get accounts information fail: invalid owner type")
    void testGetAccounts_InvalidOwnerType_Fail() throws Exception {

        mockMvc.perform(get("/accounts")
                        .param("accountOwnerType", "unicorn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid account owner type: unicorn")));
    }

    @Test
    @DisplayName("Get accounts information fail: null owner type")
    void testGetAccounts_NullOwnerType_Fail() throws Exception {

        mockMvc.perform(get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missed parameter")));
    }

    @Test
    @DisplayName("Get accounts information fail: blank owner type")
    void testGetAccounts_BlankOwnerType_Fail() throws Exception {

        mockMvc.perform(get("/accounts")
                        .param("accountOwnerType", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountOwnerType").value("Owner type cannot be empty"));
    }

    @Test
    @DisplayName("Update account status success: valid input")
    void testUpdateAccount_Success() throws Exception {
        String accountNumber = "ACC0123456789";
        String accountStatus = "INACTIVE";
        AccountStatus newStatus = AccountStatus.toValue(accountStatus);
        accountDto = new AccountDto(1L, accountNumber, AccountOwnerType.PROJECT, 10L, "Project owner", AccountType.CURRENT, Currency.USD, newStatus);

        when(userContext.getUserId()).thenReturn(10L);
        when(accountService.updateAccountStatus(accountNumber, 10L, newStatus)).thenReturn(accountDto);

        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .param("status", accountStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accountDto)))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("Update account status fail: blank account number")
    void testUpdateAccount_BlankAccountNumber_Fail() throws Exception {
        String accountNumber = "";
        String accountStatus = "INACTIVE";

        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .param("status", accountStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update account status fail: short account number")
    void testUpdateAccount_ShortAccountNumber_Fail() throws Exception {
        String accountNumber = "ACC12345";
        String accountStatus = "INACTIVE";

        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .param("status", accountStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Account number must be between 12 and 20 characters")));
    }

    @Test
    @DisplayName("Update account status fail: long account number")
    void testUpdateAccount_LongAccountNumber_Fail() throws Exception {
        String accountNumber = "ACC012345678901234567890";
        String accountStatus = "INACTIVE";

        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .param("status", accountStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Account number must be between 12 and 20 characters")));
    }

    @Test
    @DisplayName("Update account status fail: null status")
    void testUpdateAccount_NullStatus_Fail() throws Exception {
        String accountNumber = "ACC0123456789";


        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missed parameter")));
    }

    @Test
    @DisplayName("Update account status fail: invalid status")
    void testUpdateAccount_InvalidStatus_Fail() throws Exception {
        String accountNumber = "ACC0123456789";
        String status = "SECRET";

        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid account status: SECRET")));
    }

    @Test
    @DisplayName("Update account status fail: blank status")
    void testUpdateAccount_BlankStatus_Fail() throws Exception {
        String accountNumber = "ACC0123456789";
        String status = "";

        mockMvc.perform(put("/accounts/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .header("x-user-id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("New status cannot be empty")));
    }
}