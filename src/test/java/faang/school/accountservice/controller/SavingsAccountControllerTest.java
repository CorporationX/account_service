package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createAccountDto;
import static faang.school.accountservice.util.TestDataFactory.createTariffAndRateDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SavingsAccountControllerTest {
    @InjectMocks
    private SavingsAccountController savingsAccountController;
    @Mock
    private SavingsAccountService savingsAccountService;
   private MockMvc mockMvc;
   private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc  = MockMvcBuilders.standaloneSetup(savingsAccountController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void givenValidAccountIdWhenGetAccountByAccountIdThenReturnTariffAndRateObject() throws Exception {
        // given - precondition
        Long accountId = 1L;
        var expectedResult = createTariffAndRateDto();

        when(savingsAccountService.getTariffAndRateByAccountId(accountId)).thenReturn(expectedResult);

        // when - action
        var response = mockMvc.perform(get("/savingsaccount/{id}", accountId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.tariffType").value(expectedResult.tariffType()))
                .andExpect(jsonPath("$.rate").value(expectedResult.rate()))
                .andDo(print());
    }

    @Test
    void givenValidAccountNumberWhenGetAccountByClientIdThenReturnTariffAndRateObject() throws Exception {
        // given - precondition
        var expectedResult = createTariffAndRateDto();

        when(savingsAccountService.getAccountByClientId(ACCOUNT_NUMBER)).thenReturn(expectedResult);

        // when - action
        var response = mockMvc.perform(get("/savingsaccount").param("id", ACCOUNT_NUMBER));

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.tariffType").value(expectedResult.tariffType()))
                .andExpect(jsonPath("$.rate").value(expectedResult.rate()))
                .andDo(print());
    }

    @Test
    void openAccount() throws Exception {
        // given - precondition
        var accountDto = createAccountDto();

        String accountDtoJson = objectMapper.writeValueAsString(accountDto);
        when(savingsAccountService.openAccount(any(AccountDto.class))).thenReturn(accountDto);

        // when - action
        var response = mockMvc.perform(post("/savingsaccount")
                .contentType("application/json")
                .content(accountDtoJson)
        );

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(accountDto.number()))
                .andExpect(jsonPath("$.type").value(accountDto.type()))
                .andExpect(jsonPath("$.currency").value(accountDto.currency()))
                .andExpect(jsonPath("$.status").value(accountDto.status()))
                .andExpect(jsonPath("$.version").value(accountDto.version()))
                .andDo(print());
    }
}