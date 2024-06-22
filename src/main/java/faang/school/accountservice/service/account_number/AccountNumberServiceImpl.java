package faang.school.accountservice.service.account_number;

import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.util.AccountNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountNumberServiceImpl implements AccountNumberService {

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public void getUniqueAccountNumber(Consumer<FreeAccountNumber> action, AccountType accountType) {

        FreeAccountNumber accountNumber = freeAccountNumbersRepository.getAndDeleteFirst(accountType)
                .orElse(generateAccountNumber(accountType));

        action.accept(accountNumber);
    }

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public FreeAccountNumber generateAccountNumber(AccountType accountType) {

        int code = accountType.getType();
        int length = accountType.getLength();

        AccountNumberSequence accountNumberSequence = accountNumbersSequenceRepository.findByAccountType(accountType)
                .orElse(accountNumbersSequenceRepository.createSequence(accountType));

        int count = accountNumberSequence.getCount();
        accountNumbersSequenceRepository.incrementIfNumberEquals(count, accountType);

        String value = AccountNumberUtil.getAccountNumber(code, length, count);

        return FreeAccountNumber.builder()
                .number(value)
                .accountType(accountType)
                .build();
    }
}
