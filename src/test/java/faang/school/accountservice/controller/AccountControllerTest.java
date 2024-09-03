package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createAccountDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountService accountService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .build();
    }

    @Test
    void givenValidAccountNumberWhenGetAccountThenReturnAccount() throws Exception {
        // given - precondition
        var accountDto = createAccountDto();

        when(accountService.getAccountByNumber(ACCOUNT_NUMBER)).thenReturn(accountDto);

        // when - action
        var response = mockMvc.perform(get("/accounts")
                .param("number",  ACCOUNT_NUMBER));

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value(accountDto.number()))
                .andExpect(jsonPath("$.type").value(accountDto.type()))
                .andExpect(jsonPath("$.currency").value(accountDto.currency()))
                .andExpect(jsonPath("$.status").value(accountDto.status()))
                .andExpect(jsonPath("$.version").value(accountDto.version()))
                .andDo(print());
    }

    @Test
    void givenValidAccountNumberWhenOpenAccountThenReturnAccount() throws Exception {
        // given - precondition
        var accountDto = createAccountDto();

        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        when(accountService.openAccount(any(AccountDto.class))).thenReturn(accountDto);

        // when - action
        var response = mockMvc.perform(post("/accounts")
                .content(accountDtoJson)
                .contentType("application/json"));


        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(accountDto.number()))
                .andExpect(jsonPath("$.type").value(accountDto.type()))
                .andExpect(jsonPath("$.currency").value(accountDto.currency()))
                .andExpect(jsonPath("$.status").value(accountDto.status()))
                .andExpect(jsonPath("$.version").value(accountDto.version()))
                .andDo(print());
    }

    @Test
    void givenValidAccountNumberWhenBlockAccountThenAccountIsBlocked() throws Exception {
        // given - precondition
        doNothing().when(accountService).blockAccount(ACCOUNT_NUMBER);

        // when - action
        var result = mockMvc.perform(patch("/accounts/block").param("number",  ACCOUNT_NUMBER))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then - verify the output
        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody.isEmpty()).isTrue();

        verify(accountService, times(1)).blockAccount(ACCOUNT_NUMBER);
    }

    @Test
    void givenValidAccountIdWhenCloseAccountThenAccountIsClosed() throws Exception {
        // given - precondition
        doNothing().when(accountService).closeAccount(ACCOUNT_NUMBER);

        // when - action
        var result = mockMvc.perform(delete("/accounts", ACCOUNT_NUMBER).param("number",  ACCOUNT_NUMBER))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then - verify the output
        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody.isEmpty()).isTrue();

        verify(accountService, times(1)).closeAccount(ACCOUNT_NUMBER);
    }
}