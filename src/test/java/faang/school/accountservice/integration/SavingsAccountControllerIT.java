package faang.school.accountservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createAccountDtoForSaving;
import static faang.school.accountservice.util.TestDataFactory.createTariffAndRateDto;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class SavingsAccountControllerIT extends AbstractionBaseTest {
    @Value("${test.x-user-id-header}")
    private String X_USER_ID_HEADER;
    @Value("${test.user-id}")
    private String USER_ID;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void givenValidAccountIdWhenGetAccountByAccountIdThenReturnTariffAndRateObject() throws Exception {
        // given - precondition
        Long accountId = 1L;
        var expectedResult = createTariffAndRateDto();

        // when - action
        var response = mockMvc.perform(get("/savingsaccount/{id}", accountId)
                .header(X_USER_ID_HEADER, USER_ID)
        );

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

        // when - action
        var response = mockMvc.perform(get("/savingsaccount")
                .param("id", ACCOUNT_NUMBER)
                .header(X_USER_ID_HEADER, USER_ID)
        );

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
        var accountDto = createAccountDtoForSaving();
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        // when - action
        var response = mockMvc.perform(post("/savingsaccount")
                .contentType("application/json")
                .content(accountDtoJson)
                .header(X_USER_ID_HEADER, USER_ID)
        );

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.type", equalToIgnoringCase(accountDto.type())))
                .andExpect(jsonPath("$.currency", equalToIgnoringCase(accountDto.currency())))
                .andExpect(jsonPath("$.status", equalToIgnoringCase(accountDto.status())))
                .andExpect(jsonPath("$.version").value(accountDto.version()))
                .andDo(print());
    }
}