package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.IllegalStatusTransitionException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.number.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final FreeAccountNumbersService accountNumbersService;

    @Override
    public List<ResponseAccountDto> getAccounts(Long userId, Long projectId) {
        Specification<Account> spec = Specification.where(null);
        if (userId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("userId"), userId));
        }
        if (projectId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("projectId"), projectId));
        }
        log.info("Getting accounts for userId: {}, projectId: {}", userId, projectId);
        List<Account> accounts = accountRepository.findAll(spec);
        return accounts.stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ResponseAccountDto createAccount(CreateAccountDto createAccountDto) {
        validateOwner(createAccountDto.userId(), createAccountDto.projectId());

        log.info("Received request to open a new account for userId: {}, projectId: {}", createAccountDto.userId(),
                createAccountDto.projectId());
        Account account = accountMapper.toEntity(createAccountDto);
        account.setStatus(AccountStatus.OPENED);
        accountNumbersService.retrieveAccountNumber(account.getType(), account::setAccountNumber);
        Account savedAccount = accountRepository.save(account);
        log.info("Account with id: {} successfully created for userId: {} and projectId: {}", savedAccount.getId(),
                createAccountDto.userId(), createAccountDto.projectId());
        return accountMapper.toResponseDto(savedAccount);
    }

    @Override
    @Transactional
    public ResponseAccountDto updateAccountStatus(UUID accountId, AccountStatus newStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Account not found with id: %s", accountId)));
        AccountStatus currentStatus = account.getStatus();

        if (currentStatus == newStatus) {
            throw new IllegalStatusTransitionException(String.format("Account is already in status: %s", newStatus)
            );
        }

        validateStatusTransition(currentStatus, newStatus);
        applyStatusSpecificLogic(account, newStatus);

        account.setStatus(newStatus);
        Account savedAccount = accountRepository.save(account);
        log.info("Account {} status changed: {} -> {}", accountId, currentStatus, newStatus);
        return accountMapper.toResponseDto(savedAccount);
    }

    private void validateOwner(Long userId, Long projectId) {
        if ((userId != null && projectId != null) || (userId == null && projectId == null)) {
            throw new IllegalArgumentException("Must specify exactly one owner: userId or projectId");
        }
    }

    private void validateStatusTransition(AccountStatus current, AccountStatus next) {

        if (next == AccountStatus.BLOCKED && current == AccountStatus.CLOSED) {
            throw new IllegalStatusTransitionException("Cannot block closed account");
        }

        if (next == AccountStatus.OPENED && current == AccountStatus.CLOSED) {
            throw new IllegalStatusTransitionException("Cannot reopen closed account");
        }

        if (current == AccountStatus.CLOSED) {
            throw new IllegalStatusTransitionException("Cannot modify closed account");
        }
    }

    private void applyStatusSpecificLogic(Account account, AccountStatus newStatus) {
        if (newStatus == AccountStatus.CLOSED) {
            account.setClosedAt(LocalDateTime.now());
        } else if (newStatus == AccountStatus.OPENED && account.getStatus() == AccountStatus.CLOSED) {
            account.setClosedAt(null);
        }
    }
}
