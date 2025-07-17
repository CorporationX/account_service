package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.SavingsAccountTariffHistory;
import faang.school.accountservice.exception.common.DataValidationException;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingAccountRepository;
    private final TariffRepository savingAccountTariffRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final AccountValidator accountValidator;

    @Transactional
    public SavingAccount createdSavingsAccount(SavingAccount savingAccount) {
        accountValidator.validateExistsAccount(savingAccount.getId());
        //todo возможно надо разобраться не закрыт ли счет, к торорому будем привязывать накопительный
        return savingAccountRepository.save(savingAccount);
    }

    @Transactional(readOnly = true)
    public SavingAccount getSavingAccountById(UUID savingAccountId) {
        return savingAccountRepository.findById(savingAccountId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("SavingAccount with id %s was not found", savingAccountId)
                ));
    }

    @Transactional(readOnly = true)
    public SavingAccount getSavingsAccountByNumber(String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        UUID accountId = account.getId();

        return getSavingAccountById(accountId);
    }

    public void updateTariffSavingAccount(UUID savingAccountId) {
        // проверить не хотят ли обновить на тот же тариф
        // обновить тариф по счету
    }


    public void deleteSavingAccount() {
        // удалить/ удаления счет
    }


    public void depositToAccount() {

    }
    // внести на счет деньги


    public void withdraw() {

    }
    // сниять со счета деньги

    @Transactional(readOnly = true)
    public List<Long> getTariffIdsHistory(UUID savingAccountId) {
        SavingAccount account = getSavingAccountById(savingAccountId);
        List<SavingsAccountTariffHistory> tariffHistory = account.getTariffHistory();

        return tariffHistory.stream()
                .map(SavingsAccountTariffHistory::getTariff)
                .map(Tariff::getId)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getCurrentTariffId(UUID savingAccountId) {
        List<Long> tariffHistory = getTariffIdsHistory(savingAccountId);
        if (tariffHistory.isEmpty()) {
            throw new DataValidationException("нет записей о тарифах");
        }
        return tariffHistory.get(tariffHistory.size() - 1);
    }
}

