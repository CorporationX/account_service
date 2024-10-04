package faang.school.accountservice.integration;

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
import static faang.school.accountservice.util.TestDataFactory.createTariffAndRateDto;
import static java.util.List.of;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class TariffControllerIT extends AbstractionBaseTest {
    @Value("${test.x-user-id-header}")
    private String X_USER_ID_HEADER;
    @Value("${test.user-id}")
    private String USER_ID;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenAccountNumberAndRateWhenAddTariffRateThenReturnTariffAndRateObject() throws Exception {
        // given - precondition
        String tariffRate = "0.08";
        var expectedResult = createTariffAndRateDto();

        // when - action
        var response = mockMvc.perform(patch("/tariff")
                .param("number", ACCOUNT_NUMBER)
                .param("rate", tariffRate)
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
    void givenValidAccountNumberWhenGetTariffRatesThenReturnTariffRates() throws Exception {
        // given - precondition
        var expectedResult = of(0.08);

        // when - action
        var response = mockMvc.perform(get("/tariff")
                .param("number", ACCOUNT_NUMBER)
                .header(X_USER_ID_HEADER, USER_ID)
        );

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(expectedResult.size()))
                .andExpect(jsonPath("$", containsInAnyOrder(expectedResult.toArray())))
                .andDo(print());
    }
}