package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.utils.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final ValidationService validationService;
    private final NumberGenerator numberGenerator;
    private final AccountMapper mapper;

    @Transactional(readOnly = true)
    public AccountResponse getDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAllByOwnerId(Long ownerId) {
        List<Account> accounts = accountRepository.findAllByOwnerId(ownerId);
        return accounts.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public void openAccount(AccountRequest request) {
        log.info("creating account...");
        log.info("validating...");
        validationService.validateUser(request.ownerId());

        log.info("creating new account entity...");
        Account newAccount = Account.builder()
                .number(numberGenerator.generate(12, 20).toString())
                .ownerType(request.ownerType())
                .ownerId(request.ownerId())
                .currency(request.currency())
                .type(request.type())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        log.info("repository saving...");
        accountRepository.save(newAccount);
        log.info("creating account success");
    }

    @Transactional
    public void block(Long id) {
        Account account = findById(id);
        account.setStatus(AccountStatus.FROZEN);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Transactional
    public void close(Long id) {
        Account account = findById(id);
        account.setStatus(AccountStatus.CLOSED);
        account.setUpdatedAt(LocalDateTime.now());
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
    }
}
