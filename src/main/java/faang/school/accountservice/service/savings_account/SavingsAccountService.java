package faang.school.accountservice.service.savings_account;

import faang.school.accountservice.dto.savings_account.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.entity.savings_account.SavingsAccountTariffChangelog;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.exception.UniqueConstraintException;
import faang.school.accountservice.mapper.savings_account.SavingsAccountMapper;
import faang.school.accountservice.repository.savings_account.SavingsAccountRepository;
import faang.school.accountservice.repository.savings_account.SavingsAccountTariffChangelogRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountService accountService;
    private final TariffService tariffService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountTariffChangelogRepository tariffChangelogRepository;

    @Transactional
    public SavingsAccountResponse createSavingsAccount(SavingsAccountCreateDto createDto) {
        log.info("Received request to create SavingAccount based on account (ID={}) and tariff (ID={})",
                createDto.getBaseAccountId(), createDto.getTariffId());
        try {
            Account account = accountService.getAccountById(createDto.getBaseAccountId());
            Tariff tariff = tariffService.getTariffById(createDto.getTariffId());
            SavingsAccount savingsAccount = savingsAccountRepository.save(buildSavingsAccount(tariff, account));

            BigDecimal currentRate = tariffService.getCurrentTariffRateByTariffId(createDto.getTariffId());
            SavingsAccountResponse response = savingsAccountMapper.toResponse(savingsAccount);
            response.setCurrentRate(currentRate);
            response.setTariffId(tariff.getId());
            log.info("New savings account based on account (ID={}) and tariff (ID={}) was created",
                    createDto.getBaseAccountId(), createDto.getTariffId());
            return response;
        } catch (DataIntegrityViolationException ex) {
            handleUniqueConstraintViolation(ex, createDto.getBaseAccountId());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public SavingsAccountResponse getSavingsAccountById(long savingsAccountId) {
        SavingsAccount savingsAccount = savingsAccountRepository.findById(savingsAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Savings account with ID=%d was not found".formatted(savingsAccountId)));
        return null;
    }

    private SavingsAccount buildSavingsAccount(Tariff tariff, Account account) {
        SavingsAccountTariffChangelog tariffChangelog = new SavingsAccountTariffChangelog();
        tariffChangelog.setTariff(tariff);
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setTariffChangelogs(List.of(tariffChangelog));
        return savingsAccount;
    }

    private void handleUniqueConstraintViolation(DataIntegrityViolationException ex, long baseAccountId) {
        if (ex.getMessage().contains("constraint [tariff_name_key]")) {
            String exceptionMessage = String.format(
                    "Unable to set base account ID='%d' for new savings account: " +
                            "there is already an existing savings account with this base account ID.",
                    baseAccountId);
            throw new UniqueConstraintException(exceptionMessage, ex);
        }
    }
}
