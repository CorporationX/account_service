package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.ProjectDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountAccessDeniedException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.properties.AccountProperties;
import faang.school.accountservice.repository.account.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserContext userContext;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final AccountProperties accountProperties;

    @Override
    public AccountDto getAccountById(long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with id = %d not found", id)));
        checkAccessOwner(account.getOwnerId(), account.getOwnerType());
        return accountMapper.toDto(account);
    }

    @Override
    public List<AccountDto> getOwnerAccounts(long ownerId, String ownerType) {
        checkAccessOwner(ownerId, OwnerType.valueOf(ownerType));
        return accountRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType).stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public AccountDto getAccountByNumber(String number) {
        Account account = accountRepository.findByNumber(number).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with number = %s not found", number)));
        checkAccessOwner(account.getOwnerId(), account.getOwnerType());
        return accountMapper.toDto(account);
    }

    @Transactional
    @Override
    public AccountDto createAccount(AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        checkAccessOwner(account.getOwnerId(), account.getOwnerType());
        Account newAccount = accountRepository.save(buildNewAccount(account));
        return accountMapper.toDto(newAccount);
    }

    @Transactional
    @Override
    public void blockAccount(long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with id = %d not found", id)));
        checkAccessOwner(account.getOwnerId(), account.getOwnerType());
        account.setStatus(AccountStatus.FROZEN);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void closeAccount(long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with id = %d not found", id)));
        checkAccessOwner(account.getOwnerId(), account.getOwnerType());
        account.setStatus(AccountStatus.CLOSED);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private void checkAccessOwner(long ownerId, OwnerType ownerType) {
        checkExistsOwner(ownerId, ownerType);
        long currentUserId = userContext.getUserId();
        if (OwnerType.USER.equals(ownerType) && ownerId != currentUserId) {
            throw new AccountAccessDeniedException(
                    String.format("User with id = %d access denied", currentUserId));
        }
        if (OwnerType.PROJECT.equals(ownerType)) {
            ProjectDto project = projectServiceClient.getProjectById(currentUserId);
            if (project.ownerId() != currentUserId) {
                throw new AccountAccessDeniedException(
                        String.format("User with id = %d access denied to account of project with id = %d",
                                currentUserId, project.id()));
            }
        }
    }

    private void checkExistsOwner(long ownerId, OwnerType ownerType) {
        if (OwnerType.USER.equals(ownerType)) {
            if (userServiceClient.getUserById(ownerId) == null) {
                throw new IllegalArgumentException(String.format("User with id = %d not found", ownerId));
            }
        }
        if (OwnerType.PROJECT.equals(ownerType)) {
            if (projectServiceClient.getProjectById(ownerId) == null) {
                throw new IllegalArgumentException(String.format("Project with id = %d not found", ownerId));
            }
        }
    }

    private Account buildNewAccount(Account account) {
        String number = String.valueOf(generateUniqueNumber(
                accountProperties.getNumber().getMinDigits(),
                accountProperties.getNumber().getMaxDigits()));
        account.setNumber(number);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setClosedDate(LocalDateTime.now().plusYears(accountProperties.getValidityPeriod()));
        account.setVersion(1L);
        return account;
    }

    private long generateUniqueNumber(int minDigits, int maxDigits) {
        long number;
        boolean isExists;
        do {
            number = generateRandomNumber(minDigits, maxDigits);
            isExists = accountRepository.existsByNumber(String.valueOf(number));
        } while (isExists);

        return number;
    }

    private long generateRandomNumber(int minDigits, int maxDigits) {
        Random random = new Random();
        long min = (long) Math.pow(10, minDigits - 1);
        long max = (long) Math.pow(10, maxDigits) - 1;
        return min + Math.abs(random.nextLong()) % (max - min + 1);
    }
}
