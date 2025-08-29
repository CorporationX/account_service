package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserHeaderFilter;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountUpdateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.entity.account.AccountType;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserHeaderFilter userHeaderFilter;
    @MockBean
    private AccountService service;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Тестирование успешного открытия счета через контроллер")
    void createAccountTest() throws Exception {
        AccountCreateDto createDto = new AccountCreateDto(
                OwnerType.USER,
                1L,
                AccountType.CURRENT_ACCOUNT,
                Currency.USD
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тестирование успешного получения счета через контроллер")
    void getAccountTest() throws Exception {
        AccountViewDto viewDto = new AccountViewDto(
                "1000000000000000",
                OwnerType.USER,
                5L,
                AccountType.FOREIGN_CURRENCY,
                Currency.USD,
                AccountStatus.ACTIVE
        );

        when(service.getAccount(1L)).thenReturn(viewDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accountNumber").value("1000000000000000"))
                .andExpect(jsonPath("ownerId").value(5L));

    }

    @Test
    @DisplayName("Тестирование успешного изменения статуса счета через контроллер")
    void changeAccountStatusTest() throws Exception {
        AccountUpdateDto updateDto = new AccountUpdateDto(AccountStatus.CLOSED);

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

    }
}