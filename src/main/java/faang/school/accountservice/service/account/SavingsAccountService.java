package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.dto.account.TariffAndRateDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.mapper.account.SavingsAccountMapper;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.util.TariffRateUtils;
import faang.school.accountservice.validator.SavingsAccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountService accountService;
    private final SavingsAccountMapper savingsAccountMapper;
    private final SavingsAccountValidator savingsAccountValidator;
    private final AccountMapper accountMapper;
    private final TariffService tariffService;

    @Transactional
    public SavingsAccountDto create(SavingsAccountDto savingsAccountDto, String tariff) {
        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(savingsAccountDto);

        savingsAccountValidator.validateAccount(savingsAccountDto.getAccountId());
        AccountDto accountDto = accountService.getAccountById(savingsAccountDto.getAccountId());
        Account account = accountMapper.toEntity(accountDto);
        savingsAccount.setAccount(account);
        savingsAccount.setId(account.getId());

        String tariffHistory = formatTariffHistory(savingsAccount.getTariffHistory(), tariff);
        savingsAccount.setTariffHistory(tariffHistory);

        savingsAccount = savingsAccountRepository.save(savingsAccount);

        return savingsAccountMapper.toDto(savingsAccount);
    }

    @Transactional
    public SavingsAccountDto updateSavingsAccountHistory(UUID id, String tariff) {
        SavingsAccount savingsAccount = getSavingAccount(id);

        savingsAccountValidator.validateTariffForUpdates(getActualTariff(savingsAccount.getTariffHistory()), tariff);
        String tariffHistory = formatTariffHistory(savingsAccount.getTariffHistory(), tariff);
        savingsAccount.setTariffHistory(tariffHistory);

        savingsAccount = savingsAccountRepository.save(savingsAccount);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    @Transactional(readOnly = true)
    public TariffAndRateDto getSavingAccountById(UUID id) {
        SavingsAccount savingsAccount = getSavingAccount(id);
        return getTariffAndRate(savingsAccount);
    }

    @Transactional(readOnly = true)
    public TariffAndRateDto getSavingAccountByUserId(long userId) {
        SavingsAccount savingsAccount = savingsAccountRepository
                .findByAccount_OwnerIdAndAccount_OwnerType(userId, OwnerType.USER);
        return getTariffAndRate(savingsAccount);
    }

    private String formatTariffHistory(String tariffHistory, String tariff) {
        return TariffRateUtils.formatHistory(tariffHistory, tariff);
    }

    private String getActualTariff(String tariffHistory) {
        return TariffRateUtils.getLastValueFromHistory(tariffHistory);
    }

    private SavingsAccount getSavingAccount(UUID id) {
        savingsAccountValidator.validateAccountById(id);
        return savingsAccountRepository.findById(id).get();
    }

    private TariffAndRateDto getTariffAndRate(SavingsAccount savingsAccount) {
        String tariff = getActualTariff(savingsAccount.getTariffHistory());
        UUID tariffId = UUID.fromString(tariff);
        String rate = tariffService.getRateByById(tariffId);

        return new TariffAndRateDto(tariff, rate);
    }

}
