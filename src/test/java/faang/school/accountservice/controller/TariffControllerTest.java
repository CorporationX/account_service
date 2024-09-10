package faang.school.accountservice.controller;

import faang.school.accountservice.entity.RateHistory;
import faang.school.accountservice.service.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createRateHistoryList;
import static faang.school.accountservice.util.TestDataFactory.createTariffAndRateDto;
import static java.lang.Double.parseDouble;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TariffControllerTest {
    @InjectMocks
    private TariffController tariffController;
    @Mock
    private TariffService tariffService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tariffController)
                .build();
    }
    @Test
    void givenAccountNumberAndRateWhenAddTariffRateThenReturnTariffAndRateObject() throws Exception {
        // given - precondition
        String tariffRate = "0.08";
        var expectedResult = createTariffAndRateDto();

        when(tariffService.addTariffRate(ACCOUNT_NUMBER, parseDouble(tariffRate))).thenReturn(expectedResult);

        // when - action
        var response = mockMvc.perform(patch("/tariff")
                .param("number", ACCOUNT_NUMBER)
                .param("rate", tariffRate)
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
        var expectedResult = createRateHistoryList().stream()
                .map(RateHistory::getRate)
                .toList();

        when(tariffService.getTariffRates(ACCOUNT_NUMBER)).thenReturn(expectedResult);

        // when - action
        var response = mockMvc.perform(get("/tariff")
                .param("number", ACCOUNT_NUMBER)
        );

        // then - verify the output
        response.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(expectedResult.size()))
                .andExpect(jsonPath("$", containsInAnyOrder(expectedResult.toArray())))
                .andDo(print());
    }
}