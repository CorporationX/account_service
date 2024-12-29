package faang.school.accountservice.service.balance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.dto.balance.PaymentStep;
import faang.school.accountservice.dto.client.UserDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.account.AccountService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

  private static final String TEST_DATE_TIME = "2024-11-22 00:21:39";
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");

  @InjectMocks
  private BalanceServiceImpl balanceService;

  @Mock
  private BalanceRepository balanceRepository;

  @Mock
  private AccountService accountService;

  @Mock
  private UserServiceClient userServiceClient;

  @Spy
  private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);


  @Test
  @DisplayName("Should return created balance")
  void testCreateBalance() {
    BalanceCreateDto createDto = generateCreateDto();
    Balance expectedBalance = generateBalance();
    BalanceDto expectedDto = generateDto();
    long userId = 1L;
    long accountId = createDto.accountId();
    BigDecimal value = expectedDto.authorizedValue();

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(accountService.getAccount(accountId)).thenReturn(new AccountDtoResponse());
    when(balanceRepository.create(accountId, value)).thenReturn(expectedBalance);

    BalanceDto resultDto = balanceService.create(userId, createDto);

    verify(userServiceClient, times(1)).getUser(userId);
    verify(accountService, times(1)).getAccount(accountId);
    verify(balanceRepository, times(1)).create(accountId, value);
    verify(balanceMapper, times(1)).toDto(expectedBalance);

    assertEquals(expectedBalance.getAccount().getId(), resultDto.accountId());
    assertEquals(expectedBalance.getActualValue(), resultDto.actualValue());
  }

  @Test
  @DisplayName("Should return balance with updated authorization value and version")
  void testUpdatePaymentAuthorization() {
    long userId = 1L;
    PaymentDto paymentDto = generatePaymentDto(PaymentStep.AUTHORIZATION);
    long balanceId = paymentDto.balanceId();
    Balance balance = generateBalance();
    BigDecimal updatedValue = balance.getAuthorizedValue().subtract(BigDecimal.ONE);
    Balance updatedBalance = generateBalance();
    updatedBalance.setAuthorizedValue(updatedValue);
    updatedBalance.setVersion(1L);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
    when(balanceRepository.save(updatedBalance)).thenReturn(updatedBalance);

    BalanceDto resultDto = balanceService.update(userId, paymentDto);

    verify(userServiceClient, times(1)).getUser(userId);
    verify(balanceRepository, times(1)).findById(balanceId);
    verify(balanceRepository, times(1)).save(updatedBalance);

    Assertions.assertEquals(BigDecimal.valueOf(9L), resultDto.authorizedValue());
  }

  @Test
  @DisplayName("Should return balance with updated actual value and version")
  void testUpdatePaymentClearing() {
    long userId = 1L;
    PaymentDto paymentDto = generatePaymentDto(PaymentStep.CLEARING);
    long balanceId = paymentDto.balanceId();
    Balance balance = generateBalance();
    BigDecimal updatedValue = balance.getActualValue().subtract(BigDecimal.ONE);
    Balance updatedBalance = generateBalance();
    updatedBalance.setActualValue(updatedValue);
    updatedBalance.setVersion(1L);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
    when(balanceRepository.save(updatedBalance)).thenReturn(updatedBalance);

    BalanceDto resultDto = balanceService.update(userId, paymentDto);

    verify(userServiceClient, times(1)).getUser(userId);
    verify(balanceRepository, times(1)).findById(balanceId);
    verify(balanceRepository, times(1)).save(updatedBalance);

    Assertions.assertEquals(BigDecimal.valueOf(9L), resultDto.actualValue());
  }

  @Test
  @DisplayName("Should return balance")
  void testGetBalanceById() {
    long userId = 100L;
    long balanceId = 1L;
    Balance expected = generateBalance();

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(expected));

    BalanceDto resultDto = balanceService.getBalanceById(userId, balanceId);

    verify(balanceRepository, times(1)).findById(balanceId);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(expected.getActualValue(), resultDto.actualValue());
  }

  @Test
  @DisplayName("Should throw exception when balance does not exist")
  void testNegativeGetBalanceByIdBalanceDoesNptExist() {
    long userId = 100L;
    long balanceId = 1L;
    String error = "balance not found, id = " + balanceId;

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(balanceRepository.findById(balanceId)).thenThrow(new EntityNotFoundException(error));

    var exception = assertThrows(EntityNotFoundException.class,
        () -> balanceService.getBalanceById(userId, balanceId));

    verify(balanceRepository, times(1)).findById(balanceId);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(error, exception.getMessage());
  }

  @Test
  @DisplayName("Should throw exception when user does not exist")
  void testNegativeGetBalanceByIdUserDoesNotExist() {
    long userId = 100L;
    long balanceId = 1L;
    String error = String.format("User with ID %d not found", userId);

    when(userServiceClient.getUser(userId)).thenThrow(new EntityNotFoundException(error));

    var exception = assertThrows(EntityNotFoundException.class,
        () -> balanceService.getBalanceById(userId, balanceId));

    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(error, exception.getMessage());
  }

  private BalanceCreateDto generateCreateDto() {
    return BalanceCreateDto.builder()
        .accountId(2L)
        .authorizedValue(BigDecimal.TEN)
        .build();
  }

  private BalanceDto generateDto() {
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

  private Balance generateBalance() {
    return Balance.builder()
        .id(1L)
        .account(generateAccount())
        .authorizedValue(BigDecimal.TEN)
        .actualValue(BigDecimal.TEN)
        .createdAt(LocalDateTime.parse(TEST_DATE_TIME, FORMATTER))
        .updatedAt(LocalDateTime.parse(TEST_DATE_TIME, FORMATTER))
        .version(0L)
        .build();
  }

  private Account generateAccount() {
    return Account.builder()
        .id(2L)
        .accountNumber("11111")
        .owner(new Owner())
        .accountType(AccountType.CURRENT)
        .currency(Currency.EUR)
        .status(AccountStatus.ACTIVE)
        .version(0L)
        .build();
  }

  private PaymentDto generatePaymentDto(PaymentStep step) {
    return PaymentDto.builder()
        .balanceId(1L)
        .paymentStep(step)
        .value(BigDecimal.ONE)
        .build();
  }
}