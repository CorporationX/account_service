package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class AccountService {
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final AccountMapper accountMapper;
    private final Set<String> accountNumbers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public AccountDtoResponse open(@Valid AccountDtoOpen dto) {
        Account account = accountMapper.toAccount(dto);
        String accountNumber = getAccountNumber();
        account.setAccountNumber(accountNumber);
        Owner owner = ownerRepository.findOwnerByIdAndType(dto.getOwnerId(), dto.getOwnerType())
                .orElseGet(() -> ownerRepository.save(
                        Owner.builder()
                                .ownerId(dto.getOwnerId())
                                .type(dto.getOwnerType())
                                .accounts(new ArrayList<>())
                                .build()
                ));
        account.setOwner(owner);
        account.setStatus(AccountStatus.PENDING);
        Account savedAccount = accountRepository.save(account);
        owner.getAccounts().add(savedAccount);
        ownerRepository.save(owner);
        accountNumbers.add(accountNumber);
        return accountMapper.toDto(savedAccount);
    }

    private String getAccountNumber() {
        String numbers;
        do {
            numbers = generateRandomNumber();
        } while (accountNumbers.contains(numbers) || numbers.length() < 12 || numbers.length() > 20);
        return numbers;
    }

    private String generateRandomNumber() {
        StringBuilder stringNumbers = new StringBuilder();
        int length = ThreadLocalRandom.current().nextInt(12, 21);
        for (int i = 0; i < length; i++) {
            stringNumbers.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return stringNumbers.toString();
    }
}
