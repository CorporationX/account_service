package faang.school.accountservice.validation;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.controller.BalanceController;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(BalanceController.class)
@AutoConfigureMockMvc
public class AccountNumberValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;
    @MockBean
    private BalanceMapper balanceMapper;
    @MockBean
    private UserContext userContext;

    private final String getAccountUrlTemplate = "/api/v1/balances/accounts/numbers/%s";

    @ParameterizedTest
    @MethodSource("invalidAccountNumbers")
    public void testInvalidAccountNumber_ShouldFailValidation(String accountNumber) throws Exception {
        mockMvc.perform(get(getAccountUrlTemplate.formatted(accountNumber)).header("x-user-id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testValidAccountNumber_ShouldFailValidation() throws Exception {
        String validAccountNumber = "ACC1234567890";
        mockMvc.perform(get(getAccountUrlTemplate.formatted(validAccountNumber)).header("x-user-id", 1))
                .andExpect(status().isOk());
    }

    private Stream<Arguments> invalidAccountNumbers() {
        String invalidAccountNumber_1 = "123";
        String invalidAccountNumber_2 = "ACC123456789";
        return Stream.of(
               Arguments.of(invalidAccountNumber_1),
               Arguments.of(invalidAccountNumber_2),
               null
        );
    }
}