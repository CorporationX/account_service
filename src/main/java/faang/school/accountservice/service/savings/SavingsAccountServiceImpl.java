package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.savings.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.savings.SavingsAccount;
import faang.school.accountservice.repository.savings.SavingsAccountRepository;
import faang.school.accountservice.repository.savings.SavingsAccountRepository.SavingsAccountToPay;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {

  private final SavingsAccountRepository savingsAccountRepository;
  private final SavingsAccountMapper savingsAccountMapper;
  private final AccountService accountService;
  private final TariffService tariffService;
  private final TariffMapper tariffMapper;
  private final UserUtils userUtils;
  private final BalanceService balanceService;

  @Transactional
  @Override
  public SavingsAccountResponseDto add(Long userId, SavingsAccountCreateDto dto) {
    userUtils.validateUser(userId);
    AccountDtoResponse accountDto = accountService.open(dto.account());

    Long accountId = accountDto.getId();
    String tariff = "[" + dto.tariffId() + "]";
    SavingsAccount savingsAccount = savingsAccountRepository.create(accountId, tariff);
    SavingsAccountResponseDto savingsDto = savingsAccountMapper.toDto(savingsAccount);

    savingsDto.setAccount(accountDto);

    TariffDto tariffDto = tariffMapper.toDto(tariffService.findById(dto.tariffId()));

    savingsDto.setTariff(tariffDto);

    balanceService.create(userId, BalanceCreateDto.builder()
        .accountId(accountId)
        .authorizedValue(BigDecimal.ZERO)
        .build());

    return savingsDto;
  }

  @Override
  public SavingsAccount findById(Long id) {
    return savingsAccountRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Savings Account not found, id = " + id));
  }

  @Override
  public SavingsAccountResponseDto getById(Long userId, Long id) {
    SavingsAccount account = findById(id);
    Long tariffId = account.getCurrentTariffId();
    SavingsAccountResponseDto dto = savingsAccountMapper.toDto(findById(id));
    dto.getAccount().setOwnerId(account.getAccount().getOwner().getId());
    dto.setTariff(tariffMapper.toDto(tariffService.findById(tariffId)));
    return dto;
  }

  public List<SavingsAccount> getSavingsByOwner(Long ownerId) {
    return savingsAccountRepository.findAllByAccountOwnerId(ownerId);
  }

  @Transactional
  @Override
  public List<SavingsAccountResponseDto> getSavingsDtoByOwner(Long userId, Long ownerId) {
    List<SavingsAccount> savings = getSavingsByOwner(ownerId);
    return savings.stream()
        .map(account -> {
          Long tariffId = account.getCurrentTariffId();
          SavingsAccountResponseDto dto = savingsAccountMapper.toDto(account);
          dto.setTariff(tariffMapper.toDto(tariffService.findById(tariffId)));
          dto.getAccount().setOwnerId(ownerId);
          return dto;
        })
        .toList();
  }

  @Retryable(retryFor = {
      OptimisticLockException.class}, backoff = @Backoff(delay = 3000, multiplier = 2))
  @Override
  public void payToCustomers() {
    payInterestRate(getSavingsWithRates());
  }

  @Override
  @Transactional
  public List<SavingsAccountToPay> getSavingsWithRates() {
    return savingsAccountRepository.getSavingsWithRates();
  }

  private void payInterestRate(List<SavingsAccountToPay> balancesToUpdate) {
    //TODO распараллелить список накопов на несколько списков и пустить в расчет и начисление
    // сейчас тупо последовательно по списку
    balancesToUpdate.forEach(this::payToOneAccount);
  }

  private void payToOneAccount(SavingsAccountToPay account) {
    if (!account.getLastIncomeDate().equals(LocalDate.now())) {
      savingsAccountRepository.updateBalanceByIncome(account.getBalanceId(),
          account.getCurrentRate());
      savingsAccountRepository.updateSavingsAccountAfterIncome(account.getId());
      log.info("payment done!");
    }
  }

}
