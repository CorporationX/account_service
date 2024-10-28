package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.AccountStatus;
import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.model.owner.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public Account createAccount(Account account) {
        Owner owner = account.getOwner();
        Long externalId = owner.getExternalId();
        OwnerType ownerType = owner.getType();

        Owner currentOwner = ownerRepository.findOwner(externalId, ownerType)
                .orElse(createOwner(externalId, ownerType));

        String newAccountNumber = generateAccountNumber();
        account.setAccountNumber(newAccountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        account.setOwner(currentOwner);

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String number) {
        validateAccountNumber(number);
        return accountRepository.findByAccountNumber(number).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByOwner(Long externalId, OwnerType type) {
        return accountRepository.findByOwner(externalId, type);
    }

    @Transactional
    public Account suspendAccount(String number) {
        validateAccountNumber(number);
        Account account = accountRepository.findByAccountNumber(number).orElseThrow();
        if (account.getStatus().equals(AccountStatus.ACTIVE)) {
            account.setStatus(AccountStatus.SUSPENDED);
        } else {
            throw new IllegalStateException("Cannot suspend a non-ACTIVE account");
        }
        return account;
    }

    @Transactional
    public Account activateAccount(String number) {
        validateAccountNumber(number);
        Account account = accountRepository.findByAccountNumber(number).orElseThrow();
        if (account.getStatus().equals(AccountStatus.SUSPENDED)) {
            account.setStatus(AccountStatus.ACTIVE);
        } else {
            throw new IllegalStateException("Cannot activate a non-SUSPENDED account");
        }
        return account;
    }

    @Transactional
    public Account closeAccount(String number) {
        Account account = accountRepository.findByAccountNumber(number).orElseThrow();
        if (!account.getStatus().equals(AccountStatus.CLOSED)) {
            account.setStatus(AccountStatus.CLOSED);
            account.setClosedAt(LocalDateTime.now());
        } else {
            throw new IllegalStateException("Cannot close a CLOSED account");
        }
        return account;
    }

    private Owner createOwner(Long externalId, OwnerType type) {
        Object ownerDto = switch (type) {
            case USER -> userServiceClient.getUser(externalId);
            case PROJECT -> projectServiceClient.getProject(externalId);
        };

        if (Objects.isNull(ownerDto)) {
            log.error("The {}={} does not exist", type, externalId);
            throw new IllegalArgumentException("Invalid externalId");
        }

        return ownerRepository.save(Owner.builder()
                .externalId(externalId)
                .type(type)
                .build());
    }

    private String generateAccountNumber() {
        //TODO: реализовать логику генерации УНИКАЛЬНОГО номера счета
        return "00000000000000000000";
    }

    private void validateAccountNumber(String accountNumber) {
        if (!(accountNumber.length() == 20 && accountNumber.matches("\\d+"))) {
            log.error("Invalid accountNumber={}", accountNumber);
            throw new IllegalArgumentException("Invalid accountNumber");
        }
    }
}
