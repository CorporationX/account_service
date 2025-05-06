package faang.school.accountservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.constant.controller.BalanceControllerTestConstants;
import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.enums.AccountType;
import faang.school.accountservice.entity.enums.OwnerType;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BalanceControllerTest extends BalanceControllerTestConstants {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    private UpdateBalanceDto updateBalanceDto;
    private ResponseBalanceDto responseBalance;
    private Balance balance;
    private Account account;

    @BeforeEach
    void setUp() {
         account = Account.builder()
                .number(generateNumber())
                .ownerType(OwnerType.USER)
                .accountType(AccountType.CREDIT)
                .status(STATUS)
                .currency(CURRENCY)
                .build();

        balance = balanceRepository.save(Balance.builder()
                .actualBalance(ACTUAL_BALANCE)
                .authorizationBalance(AUTHORIZATION_BALANCE)
                .build());

        account.setBalance(balance);
        account = accountRepository.save(account);

        balance.setAccount(account);
        balance = balanceRepository.save(balance);
    }

    @Test
    void find_ShouldFind() {
        findTest(balance.getId(), status().isOk(), writeValueAsString(balanceMapper.toDto(balance)));
    }

    @Test
    void find_ShouldNotFindWhenBalanceIdInvalid() {
        findTest(INVALID_ID, status().isBadRequest(), FIND_INVALID_BALANCE_ID_MSG);
    }

    @Test
    void find_ShouldNotFindWhenBalanceNotExists() {
        findTest(NOT_EXISTS_ID, status().isNotFound(), BALANCE_NOT_FOUND_MSG);
    }

    @Test
    void update_ShouldUpdate() {
        updateBalanceDto = UpdateBalanceDto.builder()
                .id(balance.getId()).authorizationBalance(AUTHORIZATION).build();
        responseBalance = balanceMapper.toDto(balance);
        responseBalance.setAuthorizationBalance(AUTHORIZATION);
        updateTest(writeValueAsString(updateBalanceDto), status().isOk(), writeValueAsString(responseBalance));
    }

    @Test
    void update_ShouldNotUpdateWhenBalanceNotExists() {
        updateBalanceDto = UpdateBalanceDto.builder().id(NOT_EXISTS_ID).build();
        updateTest(writeValueAsString(updateBalanceDto), status().isNotFound(), BALANCE_NOT_FOUND_MSG);
    }

    @Test
    void update_ShouldNotUpdateWhenBalanceIdInvalid() {
        updateBalanceDto = UpdateBalanceDto.builder().id(INVALID_ID).build();
        updateTest(writeValueAsString(updateBalanceDto), status().isBadRequest(), UPDATE_INVALID_BALANCE_ID_MSG);
    }

    @Test
    void update_ShouldNotUpdateWhenAuthorizationBalanceInvalid() {
        updateBalanceDto = UpdateBalanceDto.builder().id(balance.getId()).authorizationBalance(INVALID_BALANCE).build();
        updateTest(writeValueAsString(updateBalanceDto), status().isBadRequest(), INVALID_AUTHORIZATION_BALANCE_MSG);
    }

    @Test
    void update_ShouldNotUpdateWhenActualBalanceInvalid() {
        updateBalanceDto = UpdateBalanceDto.builder().id(balance.getId()).actualBalance(INVALID_BALANCE).build();
        updateTest(writeValueAsString(updateBalanceDto), status().isBadRequest(), INVALID_ACTUAL_BALANCE_MSG);
    }

    private void findTest(long balanceId, ResultMatcher status, String msg) {
        test(get(GET_BALANCE_URL, balanceId), JSON_EMPTY, status, msg);
    }

    private void updateTest(String content, ResultMatcher status, String msg) {
        test(put(UPDATE_URL), content, status, msg);
    }

    private void test(MockHttpServletRequestBuilder endpoint,
                      String content, ResultMatcher status, String msg) {
        try {
            mockMvc.perform(endpoint
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(X_USER_ID, USER_ID)
                            .content(content))
                    .andExpect(status)
                    .andExpect(content().string(msg))
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateNumber() {
        StringBuilder builder = new StringBuilder(RANDOM.nextInt(1, 9));
        IntStream.range(1, 15).forEach(num -> builder.append(RANDOM.nextInt(0, 9)));
        return builder.toString();
    }

    private String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}