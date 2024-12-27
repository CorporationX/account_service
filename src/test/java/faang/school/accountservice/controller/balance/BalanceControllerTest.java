package faang.school.accountservice.controller.balance;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.dto.balance.PaymentStep;
import faang.school.accountservice.service.balance.BalanceService;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
@ContextConfiguration(classes = {BalanceController.class})
class BalanceControllerTest {

  private static final String TEST_DATE_TIME = "2024-11-22 00:21:39";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @MockBean
  private BalanceService balanceService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should return created Balance")
  void addBalance() throws Exception {
    long userId = 1L;
    BalanceCreateDto balanceCreateDto = getTestCreateDto();
    BalanceDto balanceDto = getTestBalanceDto();

    when(balanceService.create(userId, balanceCreateDto)).thenReturn(balanceDto);

    mockMvc.perform(post("/api/v1/balance/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(balanceCreateDto))
            .header("x-user-id", userId))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(balanceDto)))
        .andExpect(status().isOk());

    verify(balanceService, times(1)).create(userId, balanceCreateDto);
  }

  @Test
  @DisplayName("Authorization: Should return updated balance when authorize payment")
  void updateBalanceAuthorization() throws Exception {
    long userId = 1L;
    PaymentDto paymentDto = getTestPaymentDto(PaymentStep.AUTHORIZATION);
    BalanceDto updatedBalanceDto = getTestBalanceDto();

    when(balanceService.update(userId, paymentDto)).thenReturn(updatedBalanceDto);

    mockMvc.perform(put("/api/v1/balance/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(paymentDto))
            .header("x-user-id", userId))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(updatedBalanceDto)))
        .andExpect(status().isOk());

    verify(balanceService, timeout(1)).update(userId, paymentDto);
  }

  @Test
  @DisplayName("Clearing: Should return updated balance when clearing payment")
  void updateBalanceClearing() throws Exception {
    long userId = 1L;
    PaymentDto paymentDto = getTestPaymentDto(PaymentStep.CLEARING);
    BalanceDto updatedBalanceDto = getTestBalanceDto();

    when(balanceService.update(userId, paymentDto)).thenReturn(updatedBalanceDto);

    mockMvc.perform(put("/api/v1/balance/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(paymentDto))
            .header("x-user-id", userId))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(updatedBalanceDto)))
        .andExpect(status().isOk());

    verify(balanceService, timeout(1)).update(userId, paymentDto);
  }

  @ParameterizedTest
  @MethodSource("invalidPaymentDto")
  @DisplayName("Test invalid data validation")
  void negativeTest(PaymentDto paymentDto) throws Exception {
    mockMvc.perform(put("/api/v1/balance/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(paymentDto))
            .header("x-user-id", 1L))
        .andExpect(status().isBadRequest());
  }

  static Stream<Object[]> invalidPaymentDto() {
    return java.util.stream.Stream.of(
        new Object[]{PaymentDto.builder()
            .paymentStep(PaymentStep.AUTHORIZATION)
            .value(BigDecimal.ONE)
            .build()},
        new Object[]{PaymentDto.builder()
            .balanceId(1L)
            .value(BigDecimal.ONE)
            .build()},
        new Object[]{PaymentDto.builder()
            .balanceId(1L)
            .paymentStep(PaymentStep.AUTHORIZATION)
            .build()},
        new Object[]{PaymentDto.builder()
            .build()}
    );
  }

  @Test
  @DisplayName("Should return Balance by id")
  void getBalanceById() throws Exception {
    BalanceDto expectedDto = getTestBalanceDto();

    when(balanceService.getBalanceById(1L, 1L)).thenReturn(expectedDto);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/{id}", 1L)
            .header("x-user-id", 1L))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDto)))
        .andExpect(status().isOk());

    verify(balanceService, timeout(1)).getBalanceById(1L, 1L);
  }

  @Test
  @DisplayName("Return isBadRequest when header has no user id")
  void negativeTestGetBalanceNoUserId() throws Exception {
    BalanceDto expectedDto = getTestBalanceDto();
    mockMvc.perform(get("/api/v1/balance/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(expectedDto)))
        .andExpect(status().isBadRequest());
  }

  private BalanceDto getTestBalanceDto() {
    return BalanceDto.builder()
        .id(1L)
        .accountId(2L)
        .authorizedValue(BigDecimal.TEN)
        .actualValue(BigDecimal.TEN)
        .createdAt(TEST_DATE_TIME)
        .updatedAt(TEST_DATE_TIME)
        .version(0L)
        .build();
  }

  private BalanceCreateDto getTestCreateDto() {
    return BalanceCreateDto.builder()
        .accountId(2L)
        .authorizedValue(BigDecimal.TEN)
        .build();
  }

  private PaymentDto getTestPaymentDto(PaymentStep paymentStep) {
    return PaymentDto.builder()
        .balanceId(1L)
        .paymentStep(paymentStep)
        .value(BigDecimal.ONE)
        .build();
  }

}