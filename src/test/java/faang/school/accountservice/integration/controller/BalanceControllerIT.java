package faang.school.accountservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.integration.IntegrationTestBase;
import faang.school.accountservice.model.dto.balance.BalanceDto;
import faang.school.accountservice.model.dto.balance.BalanceOperationRequestDto;
import faang.school.accountservice.model.dto.balance.BalanceTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BalanceControllerIT extends IntegrationTestBase {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    private BalanceOperationRequestDto balanceOperationRequestDto;

    @BeforeEach
    void setUp() {
        balanceOperationRequestDto = new BalanceOperationRequestDto(BigDecimal.valueOf(50000));
    }

    @Test
    @DisplayName("Get Balance test - Success IT")
    void testGetBalanceSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/balances/1")
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accountId").value(1),
                        jsonPath("$.currentAuthorizationBalance").value(0.00),
                        jsonPath("$.currentActualBalance").value(100000.00),
                        jsonPath("$.createdAt").value("2024-06-01T15:00:00"),
                        jsonPath("$.updatedAt").value("2024-06-01T15:00:00"));
    }

    @Test
    @DisplayName("Get Balance doesn't exist IT")
    void testGetBalanceShouldThrowException() throws Exception {
        mockMvc.perform(get("/api/v1/balances/10")
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 10 doesn't exist!"));
    }

    @Test
    @DisplayName("Create Balance - Success IT")
    void testCreateBalanceSuccess() throws Exception {
        var balanceDto = BalanceDto.builder()
                .accountId(4)
                .currentActualBalance(BigDecimal.valueOf(90000))
                .currentAuthorizationBalance(BigDecimal.valueOf(0))
                .createdAt(LocalDateTime.parse("2024-09-01T15:00:00"))
                .updatedAt(LocalDateTime.parse("2024-09-01T15:00:00"))
                .build();

        var body = objectMapper.writeValueAsString(balanceDto);
        mockMvc.perform(post("/api/v1/balances")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.id").value(4),
                        jsonPath("$.accountId").value(4),
                        jsonPath("$.currentActualBalance").value(90000.00),
                        jsonPath("$.currentAuthorizationBalance").value(0.00));
    }

    @Test
    @DisplayName("Increase Balance increases a balance successfully")
    void testIncreaseBalanceSuccess() throws Exception {
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/1/increasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.accountId").value(1),
                        jsonPath("$.currentAuthorizationBalance").value(0.00),
                        jsonPath("$.currentActualBalance").value(150000),
                        jsonPath("$.createdAt").value("2024-06-01T15:00:00"),
                        jsonPath("$.updatedAt").isNotEmpty())
                .andDo(print());

    }

    @Test
    @DisplayName("Increase Balance throws exception")
    public void increaseBalanceThrowsException() throws Exception {
        String body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/10/increasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 10 doesn't exist!"));
    }

    @Test
    @DisplayName("Decrease Balance decreases a balance successfully")
    void decreaseBalanceShouldDecreaseBalanceSuccess() throws Exception {
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/1/decreasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.accountId").value(1),
                        jsonPath("$.currentAuthorizationBalance").value(0.00),
                        jsonPath("$.currentActualBalance").value(50000),
                        jsonPath("$.createdAt").value("2024-06-01T15:00:00"),
                        jsonPath("$.updatedAt").isNotEmpty());

    }

    @Test
    @DisplayName("Decrease Balance throws exception")
    void decreaseBalanceThrowsException() throws Exception {
        String body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/10/decreasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 10 doesn't exist!"));
    }

    @Test
    @DisplayName("Reserve Balance - Success")
    void testReserveBalanceSuccess() throws Exception {
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/1/reservation")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.accountId").value(1),
                        jsonPath("$.currentAuthorizationBalance").value(50000.00),
                        jsonPath("$.currentActualBalance").value(50000),
                        jsonPath("$.createdAt").value("2024-06-01T15:00:00"),
                        jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Reserve balance throws entity not found")
    void releaseBalanceThrowsEntityNotFoundException() throws Exception {
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/10/reservation")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 10 doesn't exist!"));
    }

    @Test
    @DisplayName("Reservation balance throws InsufficientBalanceException")
    void releaseBalanceThrowsInsufficientBalanceException() throws Exception {
        BalanceOperationRequestDto requestDto = new BalanceOperationRequestDto(BigDecimal.valueOf(10000000));
        var body = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/api/v1/balances/1/reservation")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Bad Request"),
                        jsonPath("$.message").value("Insufficient funds on balance for reservation"));
    }

    @Test
    @DisplayName("Release Balance releases balance successfully IT")
    void releaseBalanceReleasesSuccessfully() throws Exception {
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/3/releasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(3),
                        jsonPath("$.accountId").value(3),
                        jsonPath("$.currentAuthorizationBalance").value(0.00),
                        jsonPath("$.currentActualBalance").value(150000.00),
                        jsonPath("$.createdAt").value("2024-07-01T15:00:00"),
                        jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Release Balance releases throws Entity Not Found Exception IT")
    void releaseBalanceReleasesThrowsEntityNotFoundException() throws Exception {
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/32/releasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 32 doesn't exist!"));
    }

    @Test
    @DisplayName("Release Balance releases throws InsufficientBalanceException IT")
    void releaseBalanceReleasesThrowsInsufficientBalanceException() throws Exception {
        var balanceOperationRequestDto = new BalanceOperationRequestDto(BigDecimal.valueOf(10000000));
        var body = objectMapper.writeValueAsString(balanceOperationRequestDto);
        mockMvc.perform(put("/api/v1/balances/3/releasing")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("Bad Request"),
                        jsonPath("$.message").value("Cannot release more than the reserved amount"));
    }

    @Test
    @DisplayName("Transfer was successfully IT")
    void cancelReservationShouldTransferSuccessfully() throws Exception {
        var btr = BalanceTransferRequest.builder()
                .fromAccountId(1)
                .toAccountId(2)
                .amount(BigDecimal.valueOf(10000))
                .build();

        var body = objectMapper.writeValueAsString(btr);
        mockMvc.perform(put("/api/v1/balances/transferring")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.fromBalance").value(90000),
                        jsonPath("$.toBalance").value(60000));
    }

    @Test
    @DisplayName("Transfer throws IllegalArgumentException IT")
    void transferShouldThrowIllegalArgumentException() throws Exception {
        var btr = BalanceTransferRequest.builder()
                .fromAccountId(1)
                .toAccountId(1)
                .build();

        var body = objectMapper.writeValueAsString(btr);
        mockMvc.perform(put("/api/v1/balances/transferring")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.code").value("Bad Request"),
                        jsonPath("$.message").value("Cannot transfer to same account"));
    }

    @Test
    @DisplayName("Transfer throws EntityNotFoundException IT")
    void transferShouldThrowEntityNotFoundException() throws Exception {
        var btr = BalanceTransferRequest.builder()
                .fromAccountId(134)
                .toAccountId(1)
                .amount(BigDecimal.valueOf(10000))
                .build();

        var body = objectMapper.writeValueAsString(btr);
        mockMvc.perform(put("/api/v1/balances/transferring")
                        .content(body).contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 134 doesn't exist!"));
    }

    @Test
    @DisplayName("Cancel reservation cancels reservation successfully IT")
    void cancelReservationCancelsSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/v1/balances/3")
                        .header("x-user-id", 1))
                .andExpectAll(content().contentType(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.id").value(3),
                        jsonPath("$.accountId").value(3),
                        jsonPath("$.currentAuthorizationBalance").value(0.00),
                        jsonPath("$.currentActualBalance").value(200000.00),
                        jsonPath("$.createdAt").value("2024-07-01T15:00:00"),
                        jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Cancel reservation throws EntityNotFoundException IT")
    void cancelReservationThrowsEntityNotFoundException() throws Exception {
        mockMvc.perform(patch("/api/v1/balances/33")
                .header("x-user-id", 1))
                .andExpectAll(content().contentType(MediaType.APPLICATION_JSON),
                        status().isNotFound(),
                        jsonPath("$.code").value("Not Found"),
                        jsonPath("$.message").value("Balance with ID 33 doesn't exist!"));
    }
}