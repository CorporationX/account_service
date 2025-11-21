package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import feign.FeignException;
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
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public List<ResponseAccountDto> getAccountsByUserId(Long userId, Pageable pageable) {
        log.info("Received request to get account with userid: {}", userId);
        validateUserExists(userId);
        List<Account> accounts = accountRepository.findByUserId(userId, pageable);
        return accounts.stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ResponseAccountDto> getAccountsByProjectId(Long projectId, Pageable pageable) {
        log.info("Received request to get accounts for projectId: {}", projectId);
        validateProjectExists(projectId);
        List<Account> accounts = accountRepository.findByProjectId(projectId, pageable);
        return accounts.stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ResponseAccountDto createAccount(CreateAccountDto createAccountDto) {
        validateOwner(createAccountDto.userId(), createAccountDto.projectId());
        validateOwnerExists(createAccountDto.userId(), createAccountDto.projectId());

        if (accountRepository.findByAccountNumber(createAccountDto.accountNumber()).isPresent()) {
            throw new IllegalArgumentException(String.format("Account number already exists: %s",
                    createAccountDto.accountNumber()));
        }

        log.info("Received request to open a new account for userId: {}, projectId: {}", createAccountDto.userId(),
                createAccountDto.projectId());
        Account account = new Account();
        account.setAccountNumber(createAccountDto.accountNumber());
        account.setType(createAccountDto.type());
        account.setCurrency(createAccountDto.currency());
        account.setUserId(createAccountDto.userId());
        account.setProjectId(createAccountDto.projectId());
        account.setStatus(AccountStatus.OPENED);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        log.info("Account with id: {} successfully created for userId: {} and projectId: {}", savedAccount.getId(),
                createAccountDto.userId(), createAccountDto.projectId());
        return accountMapper.toResponseDto(savedAccount);
    }

    @Override
    @Transactional
    public ResponseAccountDto blockAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Account not found with id: %s", accountId)));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new IllegalArgumentException("Account is already blocked");
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot block a closed account");
        }

        account.setStatus(AccountStatus.BLOCKED);
        account.setUpdatedAt(LocalDateTime.now());
        Account savedAccount = accountRepository.save(account);
        log.info("Account blocked: {}", accountId);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Override
    @Transactional
    public ResponseAccountDto closeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Account not found with id: %s", accountId)));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalArgumentException("Account is already closed");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        Account savedAccount = accountRepository.save(account);
        log.info("Account closed: {}", accountId);
        return accountMapper.toResponseDto(savedAccount);
    }

    private void validateOwner(Long userId, Long projectId) {
        if ((userId != null && projectId != null) || (userId == null && projectId == null)) {
            throw new IllegalArgumentException("Must specify exactly one owner: userId or projectId");
        }
    }

    private void validateOwnerExists(Long userId, Long projectId) {
        if (userId != null) {
            validateUserExists(userId);
        } else {
            validateProjectExists(projectId);
        }
    }

    private void validateUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.warn("User with id {} not found in user_service", userId);
            throw new IllegalArgumentException("User not found with id: " + userId);
        } catch (FeignException e) {
            log.error("FeignException for user Id {}: {}", userId, e.getMessage());
            throw new RuntimeException("User service is unavailable");
        }
    }

    private void validateProjectExists(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException.NotFound e) {
            log.warn("Project with id {} not found in project_service", projectId);
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        } catch (FeignException e) {
            log.error("FeignException for project Id {}: {}", projectId, e.getMessage());
            throw new RuntimeException("Project service is unavailable");
        }
    }
}
