package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.service.impl.AccountServiceImpl;
import faang.school.accountservice.validator.AccountControllerValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {


    // TODO
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserContext userContext;

    @MockBean
    private AccountServiceImpl accountServiceImpl;

    @MockBean
    private AccountControllerValidator validator;

    @Autowired
    private AccountControllerValidator accountControllerValidator;

    private Long id;
    private AccountDto accountDto;
    private String number;

    @BeforeEach
    public void setup() {
        id = 1L;
        Long projectId = 2L;
        number = "123456789012345";
        accountDto = new AccountDto();
        accountDto.setId(id);
        accountDto.setProjectId(projectId);
        accountDto.setNumber(number);
        accountDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testOpenAccountSuccess() throws Exception {
        String json = objectMapper.writeValueAsString(accountDto);
        when(accountServiceImpl.openAccount(accountDto)).thenReturn(accountDto);

        accountServiceImpl.openAccount(accountDto);

        mockMvc.perform(post("/api/v1/account", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"));
    }


    @Test
    void shouldReturnAccountDto() throws Exception {
        when(accountServiceImpl.getAccount(id)).thenReturn(accountDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.projectId").value(accountDto.getProjectId()))
                .andExpect(jsonPath("$.number").value(accountDto.getNumber()));
    }

    @Test
    public void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        when(accountServiceImpl.getAccount(1L)).thenThrow(new EntityNotFoundException("Account not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAccountDtoFindByNumber() throws Exception {
        when(accountServiceImpl.getAccountNumber(number)).thenReturn(accountDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/number/{number}", number))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.projectId").value(accountDto.getProjectId()))
                .andExpect(jsonPath("$.number").value(accountDto.getNumber()));
    }

}