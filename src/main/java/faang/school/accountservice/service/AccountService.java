package faang.school.accountservice.service;

import faang.school.accountservice.dto.RateChangeRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.RateChangeRequest;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.RateChangeRequestStatus;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.exception.non_retryable.CurrencyMismatchException;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.exception.non_retryable.NotActiveAccountException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.RateChangeRequestRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    private final RateChangeRequestRepository rateChangeRequestRepository;
    private final TariffRepository tariffRepository;

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findByIdOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumberOrThrow(accountNumber);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByOwner(Long ownerId, OwnerType ownerType) {
        return accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    @Transactional
    public Account openAccount(Account account) {
        String accountNumber = accountRepository.getNextAccountNumber().toString();
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }

    @Transactional
    public Account blockAccount(Long id) {
        Account account = accountRepository.findByIdOrThrow(id);
        account.setAccountStatus(AccountStatus.BLOCKED);
        return accountRepository.save(account);
    }

    @Transactional
    public Account closeAccount(Long id) {
        Account account = accountRepository.findByIdOrThrow(id);
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    @Transactional
    public RateChangeRequest createPlannedRateChange(RateChangeRequestDto changeRequestDto) {

        validateDateChange(changeRequestDto);
        checkingIdempotenceChange(changeRequestDto);

        Tariff tariff = tariffRepository.findById(changeRequestDto.tariffId()).orElseThrow();

        RateChangeRequest changeRequest = new RateChangeRequest();
        changeRequest.setTariff(tariff);
        changeRequest.setNewRate(changeRequestDto.newRate());
        changeRequest.setStatus(RateChangeRequestStatus.PENDING);
        changeRequest.setProcessed(false);
        changeRequest.setEffectiveDate(changeRequestDto.effectiveDate());
        changeRequest.setRequestDate(LocalDate.now());

        return rateChangeRequestRepository.save(changeRequest);
    }

    @Transactional(readOnly = true)
    public void checkCurrencyMismatch(String accountNumber, Currency inputCurrency) throws CurrencyMismatchException, EntityNotFoundException {
        Account account = accountRepository.findByAccountNumberOrThrow(accountNumber);
        if (!inputCurrency.equals(account.getCurrency())) {
            throw new CurrencyMismatchException(
                    String.format("Currency of accountId %d doesn't match input currency %s",
                            account.getId(), inputCurrency)
            );
        }
    }

    @Transactional(readOnly = true)
    public void checkAccountIsActive(String accountNumber) throws NotActiveAccountException, EntityNotFoundException {
        Account account = accountRepository.findByAccountNumberOrThrow(accountNumber);
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new NotActiveAccountException(String.format(
                    "Account with id %d has not OPEN status", account.getId()));
        }
    }
}

    private void validateDateChange(RateChangeRequestDto changeRequestDto) {

        if (changeRequestDto.effectiveDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new ValidationException("Rate change must be scheduled at least one day in advance.");
        }
    }

    @Transactional(readOnly = true)
    private void checkingIdempotenceChange(RateChangeRequestDto changeRequestDto) {

        Optional<RateChangeRequest> existingRequest = rateChangeRequestRepository
                .findByTariffIdAndEffectiveDate(changeRequestDto.tariffId(), changeRequestDto.effectiveDate());

        if (existingRequest.isPresent()) {
            if (existingRequest.get().isProcessed()) {
                throw new ValidationException("Rate change already scheduled and processed.");
            } else {
                throw new ValidationException("Rate change already scheduled, processing will be retried.");
            }
        }
    }
}