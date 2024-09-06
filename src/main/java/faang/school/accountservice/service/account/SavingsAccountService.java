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

        String tariff = getActualTariff(savingsAccount.getTariffHistory());

        UUID tariffId = UUID.fromString(tariff);
        String rate = tariffService.getRateByById(tariffId);

        return new TariffAndRateDto(tariff,rate);
    }

    @Transactional(readOnly = true)
    public TariffAndRateDto getSavingAccountByUserId(long userId) {
        SavingsAccount savingsAccount = savingsAccountRepository
                .findByAccount_OwnerIdAndAccount_OwnerType(userId, OwnerType.USER);

        String tariff = getActualTariff(savingsAccount.getTariffHistory());

        UUID tariffId = UUID.fromString(tariff);
        String rate = tariffService.getRateByById(tariffId);

        return new TariffAndRateDto(tariff,rate);

    }

    private String formatTariffHistory(String tariffHistory, String tariff) {
        if(tariffHistory == null || tariffHistory.isBlank()) {
            tariffHistory = "[" + tariff + "]";
            return tariffHistory;
        }

        tariffHistory = tariffHistory.replaceAll("]", "," + tariff + "]");
        return tariffHistory;
    }

    private String getActualTariff(String tariffHistory) {
        if(tariffHistory.contains(",")) {
            String[] tariffs = tariffHistory.split(",");
            int lastTariffIndex = tariffs.length - 1;
            return tariffs[lastTariffIndex].substring(0, tariffs[lastTariffIndex].length() - 1);
        }
        else {
            return tariffHistory.substring(1, tariffHistory.length() - 1);
        }
    }

    private SavingsAccount getSavingAccount(UUID id) {
        savingsAccountValidator.validateAccountById(id);
        return savingsAccountRepository.findById(id).get();
    }
}
