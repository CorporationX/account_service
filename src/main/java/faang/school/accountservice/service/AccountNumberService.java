package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumber;
import faang.school.accountservice.repository.AccountNumberRepository;
import faang.school.accountservice.utils.CalculateExecutionTime;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.accountservice.entity.AccountNumber.Status.AVAILABLE;
import static faang.school.accountservice.entity.AccountNumber.Status.NOT_AVAILABLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountNumberService {

    @Value("${account-numbers.size}")
    private Integer accountNumberSize;

    @Value("${account-numbers.base-prefixes}")
    private List<String> prefix;

    @Value("${account-numbers.digits}")
    private String digits;

    private final AccountNumberRepository accountNumberRepository;
    private static final SecureRandom secureRandom = new SecureRandom();

//    @Scheduled(cron = "${account-numbers.scheduled}")
    @Scheduled(fixedRate = 60000 * 5)
    @CalculateExecutionTime
    public void processAccountNumber() {
        prefix.forEach(p -> {
            Collection<String> uniqueAccountNumber = createUniqueAccountNumber(p, accountNumberSize, 20 - p.length());
            accept(p, uniqueAccountNumber);
        });
    }

    public Collection<String> getUniqueAccountNumber(String prefix, Integer size) {
        Page<AccountNumber> accounts = accountNumberRepository.findAllByAccountNumberStartsWithAndStatus(prefix,
                AVAILABLE,
                PageRequest.of(0, size));
        List<AccountNumber> content = accounts.getContent();
        int realSize = content.size();
        int needGenerate = size - realSize;

        List<String> result = new ArrayList<>(content.stream()
                .map(AccountNumber::getAccountNumber)
                .toList());

        if (needGenerate > 0) {
            Collection<String> uniqueAccountNumber = createUniqueAccountNumber(prefix, needGenerate, 20 - prefix.length());
            result.addAll(uniqueAccountNumber);
            accept(prefix, uniqueAccountNumber);
        }

        accountNumberRepository.updateAccountNumbers(result, NOT_AVAILABLE);

        return result.subList(0, size);
    }


    private Collection<String> createUniqueAccountNumber(String prefix,
                                                         Integer size,
                                                         Integer accountLength) {
        List<AccountNumber> accounts = accountNumberRepository.findAllByType(prefix);

        Set<String> accountNumbers = accounts.stream()
                .map(AccountNumber::getAccountNumber)
                .collect(Collectors.toSet());

        Set<AccountNumber> availableAccounts = accounts.stream()
                .filter(account -> account.getStatus() == AVAILABLE)
                .collect(Collectors.toSet());

        Set<String> result = new HashSet<>();

        int needSize = Math.abs(availableAccounts.size() - size);
        log.info("Нужно создать " + needSize + " счетов");
        int k = 0;

        if (needSize != 0) {
            while (k < needSize) {
                StringBuffer stringBuffer = new StringBuffer(prefix);
                for (int i = 0; i < accountLength; i++) {
                    stringBuffer.append(digits.charAt(secureRandom.nextInt(digits.length())));
                }
                boolean isAdded = accountNumbers.add(stringBuffer.toString());
                if (isAdded) {
                    k++;
                    result.add(stringBuffer.toString());
                }
            }
        }
        return result;
    }

    private List<AccountNumber> accept(String prefix, Collection<String> numbers) {
        List<AccountNumber> newAccountNumbers = numbers.stream()
                .map(accounNumber -> {
                    AccountNumber newAccountNumber = new AccountNumber();
                    newAccountNumber.setAccountNumber(accounNumber);
                    newAccountNumber.setStatus(AVAILABLE);
                    newAccountNumber.setType(prefix);
                    return newAccountNumber;
                }).toList();

        return accountNumberRepository.saveAllAndFlush(newAccountNumbers);
    }
}
