package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.TariffAndRateMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final TariffAndRateMapper tariffRateMapper;
    private final AccountMapper accountMapper;
    private final FreeAccountNumberService freeAccountNumberService;

    public TariffAndRateDto getTariffAndRateByAccountId(Long accountId) {
        return savingsAccountRepository.findById(accountId)
                .map(tariffRateMapper::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("Savings account is not found."));
    }

    public TariffAndRateDto getAccountByClientId(String number) {
        return savingsAccountRepository.findSavingsAccountByAccountNumber(number)
                .map(tariffRateMapper::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("Savings account is not found."));
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        log.info("Opening a new savings account: {}", accountDto);

        var savedAccount = createAndSaveAccount(accountDto);
        log.debug("Saved account with ID: {}", savedAccount.getId());

        createAndSaveSavingsAccount(savedAccount);
        log.debug("Created and saved savings account associated with account ID: {}", savedAccount.getId());

        log.info("Successfully opened a savings account with number: {}", savedAccount.getNumber());
        return accountMapper.toDto(savedAccount);
    }

    private Account createAndSaveAccount(AccountDto accountDto){
        String accountNumber = freeAccountNumberService.getNextSavingsAccountNumber();
        log.debug("Generated account number: {}", accountNumber);

        var account = accountMapper.toEntity(accountDto);
        log.debug("Mapping AccountDto to Account entity for: {}", accountDto);

        account.setNumber(accountNumber);
        return accountRepository.save(account);
    }

    private void createAndSaveSavingsAccount(Account savedAccount){
        log.debug("Creating SavingsAccount entity for account ID: {}", savedAccount.getId());
        var savingsAccount = SavingsAccount.builder()
                .account(savedAccount)
                .lastInterestCalculationDate(now())
                .createdAt(now())
                .updatedAt(now())
                .version(1)
                .build();
        savingsAccountRepository.save(savingsAccount);
        log.debug("SavingsAccount successfully saved for account ID: {}", savedAccount.getId());
    }
}
