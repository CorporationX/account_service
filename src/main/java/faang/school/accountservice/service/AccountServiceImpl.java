package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<ResponseAccountDto> getAccountsByUserId(Long userId, Pageable pageable) {
        log.info("Received request to get account with userid: {}", userId);
        List<Account> accounts = accountRepository.findByUserId(userId, pageable);
        return accounts.stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ResponseAccountDto> getAccountsByProjectId(Long projectId, Pageable pageable) {
        log.info("Received request to get accounts for projectId: {}", projectId);
        List<Account> accounts = accountRepository.findByProjectId(projectId, pageable);
        return accounts.stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ResponseAccountDto createAccount(CreateAccountDto createAccountDto, Long userId, Long projectId) {
        validateOwner(userId, projectId);

        if (accountRepository.findByAccountNumber(createAccountDto.accountNumber()).isPresent()) {
            throw new IllegalArgumentException(String.format("Account number already exists: %s", createAccountDto.accountNumber()));
        }

        log.info("Received request to open a new account for userId: {}, projectId: {}", userId, projectId);
        Account account = new Account();
        account.setAccountNumber(createAccountDto.accountNumber());
        account.setType(createAccountDto.type());
        account.setCurrency(createAccountDto.currency());
        account.setUserId(userId);
        account.setProjectId(projectId);
        account.setStatus(AccountStatus.OPENED);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        log.info("Account with id: {} successfully created for userId: {} and projectId: {}", savedAccount.getId(), userId, projectId);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Override
    @Transactional
    public ResponseAccountDto blockAccount(UUID accountId, Long userId, Long projectId) {
        validateOwner(userId, projectId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account not found with id: %s", accountId)));

        if (!isOwner(account, userId, projectId)) {
            throw new IllegalArgumentException("Account does not belong to specified owner");
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new IllegalArgumentException("Account is already blocked");
        }

        account.setStatus(AccountStatus.BLOCKED);
        Account savedAccount = accountRepository.save(account);
        log.info("Account blocked: {}", accountId);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Override
    @Transactional
    public ResponseAccountDto closeAccount(UUID accountId, Long userId, Long projectId) {
        validateOwner(userId, projectId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account not found with id: %s", accountId)));

        if (!isOwner(account, userId, projectId)) {
            throw new IllegalArgumentException("Account does not belong to specified owner");
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalArgumentException("Account is already closed");
        }

        account.setStatus(AccountStatus.CLOSED);
        Account savedAccount = accountRepository.save(account);
        log.info("Account closed: {}", accountId);
        return accountMapper.toResponseDto(savedAccount);
    }

    private void validateOwner(Long userId, Long projectId) {
        if ((userId != null && projectId != null) || (userId == null && projectId == null)) {
            throw new IllegalArgumentException("Must specify exactly one owner: userId or projectId");
        }
    }

    private boolean isOwner(Account account, Long userId, Long projectId) {
        if (userId != null) {
            return userId.equals(account.getUserId());
        } else if (projectId != null) {
            return projectId.equals(account.getProjectId());
        }
        return false;
    }
}
