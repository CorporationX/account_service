package faang.school.accountservice.service.savingsaccount;

import faang.school.accountservice.dto.savingsaccount.DepositDto;
import faang.school.accountservice.dto.savingsaccount.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savingsaccount.WithdrawDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.exception.SavingsAccountNotFoundException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final TariffRepository tariffRepository;
    private final SavingsAccountMapper savingsAccountMapper;

    @Override
    @Transactional
    public SavingsAccountResponseDto openSavingsAccount(
            OpenSavingsAccountDto accountDto
    ) {
        existsSavingsAccountByAccountId(accountDto.getAccountId());
        Account account = findAccountById(accountDto.getAccountId());
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setTariffHistory(new ArrayList<>(List.of(accountDto.getInitialTariffId())));
        savingsAccount.setLastInterestDate(LocalDateTime.now());
        savingsAccountRepository.save(savingsAccount);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Override
    @Transactional
    public SavingsAccountResponseDto getSavingsAccountById(String accountId) {
        SavingsAccount account = findSavingsAccountByAccountId(accountId);
        return savingsAccountMapper.toSavingsAccountResponseDto(account);
    }

    @Override
    @Transactional
    public SavingsAccountResponseDto changeTariff(
            Long savingsAccountId, Long newTariffId
    ) {
        SavingsAccount savingsAccount = findSavingsAccountById(savingsAccountId);
        existsTariffById(newTariffId);
        savingsAccount.addTariffId(newTariffId);
        savingsAccountRepository.save(savingsAccount);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Override
    @Transactional
    public SavingsAccountResponseDto deposit(DepositDto depositDto) {
        SavingsAccount savingsAccount = findSavingsAccountById(depositDto.getSavingsAccountId());
        performDeposit(savingsAccount, depositDto.getAmount());
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    @Override
    @Transactional
    public SavingsAccountResponseDto withdraw(WithdrawDto withdrawDto) {
        SavingsAccount savingsAccount = findSavingsAccountById(withdrawDto.getSavingsAccountId());
        BigDecimal amount = withdrawDto.getAmount();
        validateSufficientFunds(savingsAccount, withdrawDto.getAmount());
        performWithdrawal(savingsAccount, amount);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccount);
    }

    private void existsSavingsAccountByAccountId(String accountId) {
        if (savingsAccountRepository.existsByAccountId(accountId)) {
            String message = String.format("Tariff not found with id: %s", accountId);
            log.error(message);
            throw new IllegalStateException(message);
        }
    }

    private void existsTariffById(Long tariffId) {
        if (tariffRepository.existsById(tariffId)) {
            String message = String.format("Tariff not found with id: %d", tariffId);
            log.error(message);
            throw new TariffNotFoundException(message);
        }
    }

    private Account findAccountById(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    String message = String.format("Account not found with id: %s", accountId);
                    log.error(message);
                    return new AccountNotFoundException(message);
                });
    }

    private SavingsAccount findSavingsAccountById(Long savingsAccountId) {
        return savingsAccountRepository.findById(savingsAccountId)
                .orElseThrow(() -> {
                    String message = String.format("Account not found with id: %d", savingsAccountId);
                    log.error(message);
                    return new SavingsAccountNotFoundException(message);
                });
    }

    private SavingsAccount findSavingsAccountByAccountId(String accountId) {
        return savingsAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    String message = String.format("Account not found with id: %s", accountId);
                    log.error(message);
                    return new SavingsAccountNotFoundException(message);
                });
    }

    private void performDeposit(SavingsAccount account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        log.info("Account {} balance updated from {} to {}",
                account.getId(), account.getBalance(), newBalance);
    }

    private void validateSufficientFunds(SavingsAccount account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            String message = String.format("Insufficient funds in account %d. Current balance: %.2f, requested: %.2f",
                    account.getId(), account.getBalance(), amount);
            log.warn(message);
            throw new InsufficientFundsException(message);
        }
    }

    private void performWithdrawal(SavingsAccount account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        log.info("Account {} balance updated from {} to {}",
                account.getId(), account.getBalance(), newBalance);
    }
}
