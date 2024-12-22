package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.mapper.account.CreateAccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.validator.account.AccountServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CreateAccountMapper createAccountMapper;
    private final AccountServiceValidator validator;

    public List<AccountDto> getAccount(long ownerId, long ownerType) {
        log.info("validate ownerId and ownerType");
        validator.checkId(ownerId, ownerType);

        List<Account> account = getAccountByOwnerIdAndType(ownerId, ownerType);

        log.info("mapping list of account to list of AccountDto and return");
        return account.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    public AccountDto openNewAccount(CreateAccountDto createAccountDto) {
        log.info("validate createAccountDto");
        validator.checkId(createAccountDto.getOwnerId());
        validator.validateCreateAccountDto(createAccountDto);

        log.info("mapping createAccountDto to account");
        Account account = createAccountMapper.toEntity(createAccountDto);
        account.setStatus(Status.ACTIVE);

        createAccountNumberAndSave(account);

        log.info("mapping account to accountDto");
        return accountMapper.toDto(account);
    }

    public AccountDto changeStatus(long accountId, Status status) {
        log.info("validate accountId");
        validator.checkId(accountId);

        Account account = getAccountById(accountId);
        account.setStatus(status);

        if (status == Status.CLOSED) {
            log.info("setting closetAt as now");
            account.setClosedAt(LocalDateTime.now());
        }

        log.info("update account at db");
        accountRepository.save(account);

        log.info("mapping account to accountDto");
        return accountMapper.toDto(account);
    }

    private Account getAccountById(long accountId) {
        log.info("validate accountId");
        validator.checkId(accountId);

        log.info("getting account from db by id");
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("account with id = %d does not exist", accountId)));
    }

    private List<Account> getAccountByOwnerIdAndType(long ownerId, long ownerType) {
        log.info("validate ownerId and ownerType");
        validator.checkId(ownerId, ownerType);

        log.info("getting ownerType by id");
        OwnerType owner = OwnerType.getOwnerById(ownerType);

        log.info("get all user or project account from db");
        return accountRepository.findByOwnerIdAndOwnerType(ownerId, owner);
    }

    //TODO заглушка. исправить на актуальную версию генерации при выполнении задачи на генерацию уникальных счетов
    private void createAccountNumberAndSave(Account account){
        int maxAttempts = 10;
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                account.setAccountNumber(generateRandomAccountNumber());
                accountRepository.save(account);
                break;
            } catch (Exception e) {
                attempts++;
            }
        }

        if (attempts == maxAttempts) {
            throw new IllegalArgumentException("Cant create accountNumber pls try later");
        }
    }
    //TODO заглушка. исправить на актуальную версию генерации при выполнении задачи на генерацию уникальных счетов
    private String generateRandomAccountNumber() {
        String DIGITS = "0123456789";
        SecureRandom random = new SecureRandom();

        int length = 12 + random.nextInt(9);

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(DIGITS.length());
            result.append(DIGITS.charAt(index));
        }

        return result.toString();
    }
}
