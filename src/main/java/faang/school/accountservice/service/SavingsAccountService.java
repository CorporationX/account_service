package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffHistory;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;
    private final AccountRepository accountRepository;
    private final TariffRepository tariffRepository;

    @Transactional
    public SavingsAccountDto createSavingAccount(Long accountId, Long tariffId) {
        log.info("Trying create saving account.");

        Account account = accountRepository.findById(accountId);
        Tariff tariff = checkTariffId(tariffId);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setVersion(0);
        savingsAccount.setLastInterestDate(LocalDate.from(LocalDateTime.now()));
        savingsAccount.setCreatedAt(LocalDateTime.now());
        savingsAccount.setUpdatedAt(LocalDateTime.now());

        TariffHistory tariffHistory = new TariffHistory();
        tariffHistory.setSavingsAccount(savingsAccount);
        tariffHistory.setTariffId(tariffId);
        savingsAccount.setTariffHistory(List.of(tariffHistory));

        return savingsAccountMapper.toDto(savingsAccount);
    }

    public SavingsAccountDto getSavingsAccountById(Long id) {
        log.info("Trying to getting account by id {}", id);
        SavingsAccount savingsAccount = findSavingsAccounts(() -> savingsAccountRepository.findById(id), id);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    public SavingsAccountDto getSavingsAccountByAccountId(Long accountId) {
        SavingsAccount savingsAccount = findSavingsAccounts(() -> savingsAccountRepository.findByAccountId(accountId), accountId);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    private SavingsAccount findSavingsAccounts(Supplier<Optional<SavingsAccount>> supplier, Long id) {
        return supplier.get().orElseThrow(() -> {
            log.error("No account found with this id({})", id);
            return new EntityNotFoundException(String.format("No account found with this id(%s)", id));
        });
    }

    private Tariff checkTariffId(Long tariffId) {
        return tariffRepository.findById(tariffId).orElseThrow(() -> {
            log.error("");
            return new EntityNotFoundException(String.format("No tariff with this id %s", tariffId));
        });
    }
}
