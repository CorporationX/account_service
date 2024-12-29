package faang.school.accountservice.service.balance;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.account.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

  private final BalanceRepository balanceRepository;
  private final BalanceMapper balanceMapper;
  private final AccountService accountService;
  private final UserServiceClient userServiceClient;

  @Transactional
  @Override
  public BalanceDto create(Long userId, BalanceCreateDto balanceCreateDto) {
    validateUser(userId);
    long accountId = balanceCreateDto.accountId();
    accountService.getAccount(accountId);
    Balance balance = balanceRepository.create(accountId,
        balanceCreateDto.authorizedValue());
    return balanceMapper.toDto(balance);
  }

  @Transactional
  @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
  @Override
  public BalanceDto update(Long userId, PaymentDto paymentDto) {
    validateUser(userId);
    Long id = paymentDto.balanceId();
    BigDecimal value = paymentDto.value();

    Balance balance = switch (paymentDto.paymentStep()) {
      case AUTHORIZATION -> getAuthorizationBalance(id, value);
      case CLEARING -> getClearingBalance(id, value);
      case UP_BALANCE -> getUpBalance(id, value);
    };
    return balanceMapper.toDto(balanceRepository.save(balance));
  }

  @Override
  public Balance findBalanceById(Long id) {
    return balanceRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("balance not found, id = " + id));
  }

  @Override
  public BalanceDto getBalanceById(Long userId, Long id) {
    validateUser(userId);
    return balanceMapper.toDto(findBalanceById(id));
  }

  private void validateUser(Long userId) {
    if (userServiceClient.getUser(userId) == null) {
      throw new EntityNotFoundException(String.format("User with ID %d not found", userId));
    }
    log.info("user with id = {} validated", userId);
  }

  private Balance getAuthorizationBalance(Long id, BigDecimal value) {
    Balance balance = findBalanceById(id);
    if (balance.getAuthorizedValue().compareTo(value) < 0) {
      throw new IllegalArgumentException("Not enough money to authorize payment");
    }
    balance.authorizePayment(value);
    return balance;
  }

  private Balance getClearingBalance(Long id, BigDecimal value) {
    Balance balance = findBalanceById(id);
    if (balance.getActualValue().compareTo(value) < 0) {
      throw new IllegalArgumentException("Not enough money to clear payment");
    }
    balance.clearPayment(value);
    return balance;
  }

  private Balance getUpBalance(Long id, BigDecimal value) {
    Balance balance = findBalanceById(id);
    balance.upBalance(value);
    return balance;
  }

}
