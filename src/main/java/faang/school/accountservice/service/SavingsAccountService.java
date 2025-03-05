package faang.school.accountservice.service;

import faang.school.accountservice.dto.savings_account.CreateSavingsAccountRequest;
import faang.school.accountservice.dto.savings_account.SavingsAccountDto;
import faang.school.accountservice.dto.savings_account.UpdateTariffSavingsAccountRequest;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final TariffService tariffService;
    private final AccountService accountService;

    @Transactional
    public SavingsAccountDto create(@Valid CreateSavingsAccountRequest createSavingsAccountRequest) {
        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(createSavingsAccountRequest);

        if (savingsAccount.getBalance() == null) {
            savingsAccount.setBalance(new BigDecimal(0));
        }

        Tariff tariff = tariffService.getTariffById(createSavingsAccountRequest.startTariffId());
        Account account = accountService.getAccountById(createSavingsAccountRequest.accountId());

        if (account.getSavingsAccount() != null) {
            throw new EntityExistsException("Savings account already exists");
        }

        savingsAccount.setAccount(account);
        savingsAccount.setTariffHistoryIds(List.of(tariff.getId()));
        savingsAccount =  savingsAccountRepository.save(savingsAccount);

        return savingsAccountMapper.toSavingsAccountDto(savingsAccount, tariff.getActualRate());
    }

    @Transactional(readOnly = true)
    public SavingsAccountDto getById(Long id) {
        SavingsAccount savingsAccount = findById(id);
        Tariff tariff = tariffService.getTariffById(savingsAccount.getActualTariffId());
        return savingsAccountMapper.toSavingsAccountDto(savingsAccount, tariff.getActualRate());
    }

    @Transactional(readOnly = true)
    public SavingsAccountDto getByAccountId(Long accountId) {
        SavingsAccount savingsAccount = findByAccountId(accountId);
        Tariff tariff = tariffService.getTariffById(savingsAccount.getActualTariffId());
        return savingsAccountMapper.toSavingsAccountDto(savingsAccount, tariff.getActualRate());
    }

    @Transactional(readOnly = true)
    public SavingsAccount findById(@Valid @NotNull @Positive Long id) {
        return savingsAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SavingsAccount not found with id " + id));
    }

    @Transactional(readOnly = true)
    public SavingsAccount findByAccountId(@Valid @NotNull @Positive Long accountId) {
        return savingsAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("SavingsAccount not found with id " + accountId));
    }

    @Transactional
    public SavingsAccountDto addTariff(@Valid UpdateTariffSavingsAccountRequest updateTariffSavingsAccountRequest) {
        Tariff tariff = tariffService.getTariffById(updateTariffSavingsAccountRequest.tariffId());
        SavingsAccount savingsAccount = findById(updateTariffSavingsAccountRequest.id());

        savingsAccount.getTariffHistoryIds().add(tariff.getId());
        return savingsAccountMapper.toSavingsAccountDto(savingsAccount, tariff.getActualRate());
    }
}
