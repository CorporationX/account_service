package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.dto.savings.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.savings.SavingsAccount;
import faang.school.accountservice.repository.savings.SavingsAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {

  private final SavingsAccountRepository savingsAccountRepository;
  private final SavingsAccountMapper savingsAccountMapper;
  private final AccountService accountService;
  private final AccountMapper accountMapper;
  private final TariffService tariffService;
  private final TariffMapper tariffMapper;
  private final UserUtils userUtils;

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

    return savingsDto;
  }

  @Override
  public SavingsAccount findById(Long id) {
    return savingsAccountRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Savings Account not found, id = " + id));
  }

  //TODO AK: @Scheduled generate account numbers for AccountType.SAVINGS - separate method

  //TODO AK: @Scheduled (or other way) every day calculate %% and update balances for SavingsAccounts - use @Retry, user Multithreading

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
          System.out.println("*******" + account.getAccount().getOwner().getId());
          dto.setTariff(tariffMapper.toDto(tariffService.findById(tariffId)));
          dto.getAccount().setOwnerId(ownerId);
          return dto;
        })
        .toList();
  }

}
