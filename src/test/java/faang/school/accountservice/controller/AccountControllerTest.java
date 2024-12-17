package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.ConflictException;
import faang.school.accountservice.handler.GlobalExceptionHandler;
import faang.school.accountservice.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {AccountController.class, AccountService.class, GlobalExceptionHandler.class})
public class AccountControllerTest {

    private final static String GET_URL = "/accounts/{accountId}";
    private final static String POST_URL_OPEN = "/accounts/open";
    private final static String POST_URL_BLOCK = "/accounts/{accountId}/block";
    private final static String POST_URL_CLOSE = "/accounts/{accountId}/close";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAccountSuccess() throws Exception {
        AccountDto accountDto = initializeAccountDto();

        Mockito.when(accountService.getAccount(1L)).thenReturn(accountDto);

        mockMvc.perform(get(GET_URL, 1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(accountDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountNotFound() throws Exception {
        Long accountId = 1L;
        Mockito.when(accountService.getAccount(accountId)).thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get(GET_URL, accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    void openAccountSuccess() throws Exception {
        AccountDto accountDto = initializeAccountDto();
        Mockito.when(accountService.openAccount(accountDto)).thenReturn(accountDto);

        mockMvc.perform(post(POST_URL_OPEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(accountDto)))
                .andExpect(status().isOk());
    }

    @Test
    void openAccountConflict() throws Exception {
        AccountDto accountDto = initializeAccountDto();
        Mockito.when(accountService.openAccount(accountDto)).thenThrow(new ConflictException("Account with number 1234567890 already exists"));

        mockMvc.perform(post(POST_URL_OPEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(accountDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void blockAccountSuccess() throws Exception {
        AccountDto accountDto = initializeAccountDto();
        accountDto.setStatus(AccountStatus.BLOCKED);
        Mockito.when(accountService.blockAccount(1L)).thenReturn(accountDto);

        mockMvc.perform(post(POST_URL_BLOCK, 1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(accountDto)))
                .andExpect(status().isOk());
    }

    @Test
    void blockAccountNotFound() throws Exception {
        Long accountId = 1L;
        Mockito.when(accountService.blockAccount(accountId)).thenThrow(new EntityNotFoundException("Account not found"));

        mockMvc.perform(post(POST_URL_BLOCK, accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    void closeAccountSuccess() throws Exception {
        AccountDto accountDto = initializeAccountDto();
        accountDto.setStatus(AccountStatus.CLOSED);
        Mockito.when(accountService.closeAccount(1L)).thenReturn(accountDto);

        mockMvc.perform(post(POST_URL_CLOSE, 1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(accountDto)))
                .andExpect(status().isOk());
    }

    @Test
    void closeAccountNotFound() throws Exception {
        Long accountId = 1L;
        Mockito.when(accountService.closeAccount(accountId)).thenThrow(new EntityNotFoundException("Account not found"));

        mockMvc.perform(post(POST_URL_CLOSE, accountId))
                .andExpect(status().isNotFound());
    }


    private AccountDto initializeAccountDto() {
        return AccountDto.builder()
                .id(1L)
                .number("1234567890")
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .version(1)
                .build();
    }
}
