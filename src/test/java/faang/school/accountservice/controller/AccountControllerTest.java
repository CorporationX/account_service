package faang.school.accountservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AccountService accountService;

    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final long USER_ID = 2;
    private static final long PROJECT_ID = 1;
    private static final String ACCOUNT_NUMBER = "12345678912345";

    @Test
    @DisplayName("200 ОК - POST /v1/accounts")
    void positive_shouldCallCreateAccount() throws Exception {
        CreateAccountDto createAccountDto = createAccountDto();
        AccountDto accountDto = createExistsAccountDto(createAccountDto.userId(), createAccountDto.projectId());
        when(accountService.create(any(CreateAccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(post("/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(createAccountDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(accountDto), true));

        verify(accountService, times(1)).create(createAccountDto);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/accounts/id/{id}")
    void positive_shouldFindAccountById() throws Exception {
        AccountDto accountDto = createExistsAccountDto(USER_ID, null);
        when(accountService.findById(ACCOUNT_ID)).thenReturn(accountDto);

        mockMvc.perform(get("/v1/accounts/id/{id}", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(accountDto), true))
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID.toString()));

        verify(accountService, times(1)).findById(ACCOUNT_ID);
    }

    @Test
    @DisplayName("200 OK - GET /v1/accounts/number/{number}")
    void positive_shouldFindAccountByNumber() throws Exception {
        AccountDto accountDto = createExistsAccountDto(USER_ID, null);
        when(accountService.findByNumber(ACCOUNT_NUMBER)).thenReturn(accountDto);

        mockMvc.perform(get("/v1/accounts/number/{number}", ACCOUNT_NUMBER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(accountDto), true))
                .andExpect(jsonPath("$.number").value(ACCOUNT_NUMBER));

        verify(accountService, times(1)).findByNumber(ACCOUNT_NUMBER);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/accounts/users/{id}")
    void positive_shouldFindAllByUserId() throws Exception {
        AccountDto accountDto = createExistsAccountDto(USER_ID, null);
        when(accountService.findByUserId(USER_ID)).thenReturn(List.of(accountDto));

        mockMvc.perform(get("/v1/accounts/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(List.of(accountDto)), true))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[*].userId", everyItem(is((int) USER_ID))));

        verify(accountService, times(1)).findByUserId(USER_ID);
    }

    @Test
    @DisplayName("200 ОК - GET /v1/accounts/projects/{id}")
    void positive_shouldFindAllByProjectId() throws Exception {
        AccountDto accountDto = createExistsAccountDto(null, PROJECT_ID);
        when(accountService.findByProjectId(PROJECT_ID)).thenReturn(List.of(accountDto));

        mockMvc.perform(get("/v1/accounts/projects/{id}", PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(toJson(List.of(accountDto)), true))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[*].projectId", everyItem(is((int) PROJECT_ID))));

        verify(accountService, times(1)).findByProjectId(PROJECT_ID);
    }

    @Test
    @DisplayName("200 ОК - PUT /v1/accounts/{id}/block")
    void positive_shouldBlockAccount() throws Exception {
        mockMvc.perform(put("/v1/accounts/{id}/block", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(accountService, times(1)).block(ACCOUNT_ID);
    }

    @Test
    @DisplayName("200 ОК - PUT /v1/accounts/{id}/close")
    void positive_shouldCloseAccount() throws Exception {
        mockMvc.perform(put("/v1/accounts/{id}/close", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(accountService, times(1)).close(ACCOUNT_ID);
    }

    private static Stream<Arguments> provideNotValidAccount() {
        return Stream.of(
                Arguments.of(new CreateAccountDto(USER_ID, null, null, Currency.RUB)),
                Arguments.of(new CreateAccountDto(USER_ID, null, AccountType.PERSONAL_CURRENT, null)));
    }

    @ParameterizedTest
    @MethodSource("provideNotValidAccount")
    @DisplayName("400 Bad Request - POST /v1/accounts - поля тела не валидны")
    void negative_whenDataDtoNotValid_returns400BadRequest(CreateAccountDto accountDto) throws Exception {
        mockMvc.perform(post("/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(accountDto)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).create(any(CreateAccountDto.class));
    }

    // ----------------------------

    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private CreateAccountDto createAccountDto() {
        return new CreateAccountDto(USER_ID, null, AccountType.PERSONAL_CURRENT, Currency.RUB);
    }

    private AccountDto createExistsAccountDto(Long userId, Long projectId) {
        return AccountDto.builder()
                .id(ACCOUNT_ID)
                .number(ACCOUNT_NUMBER)
                .userId(userId)
                .projectId(projectId)
                .accountType(AccountType.PERSONAL_CURRENT)
                .currency(Currency.RUB)
                .createdAt(LocalDateTime.now())
                .status(AccountStatus.ACTIVE)
                .build();
    }
}