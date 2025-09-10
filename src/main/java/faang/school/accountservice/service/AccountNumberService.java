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
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.accountservice.entity.AccountNumber.Status.AVAILABLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountNumberService {

    @Value("${account-numbers.size}")
    private Integer accountNumberSize;
    private final AccountNumberRepository accountNumberRepository;
    private static final String PREFIX = "BANK";
    private static final Integer ACCOUNT_LENGTH = 20 - PREFIX.length();
    private static final String DIGITS = "0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

//    @Scheduled(fixedRate = 60000 * 5)
    @CalculateExecutionTime
    public void processAccountNumber() {
        List<String> prefixes = List.of("BANK", "ACCOUNT", "DEPOSIT", "PAYMENT");

        prefixes.forEach(prefix -> {
            Collection<String> result = createUniqueAccountNumber(prefix, accountNumberSize,
                    20 - prefix.length());

            List<AccountNumber> newAccountNumbers = result.stream()
                    .map(accounNumber -> {
                        AccountNumber newAccountNumber = new AccountNumber();
                        newAccountNumber.setAccountNumber(accounNumber);
                        newAccountNumber.setStatus(AVAILABLE);
                        newAccountNumber.setType(prefix);
                        return newAccountNumber;
                    }).toList();

            accountNumberRepository.saveAllAndFlush(newAccountNumbers);
        });
    }

    public Collection<String> generateAccountNumber(@Nullable String prefix,
                                                    Integer size,
                                                    Integer accountLength) {
        Collection<String> result = createUniqueAccountNumber(prefix, size, accountLength);
        List<AccountNumber> newAccountNumbers = result.stream()
                .map(accounNumber -> {
                    AccountNumber newAccountNumber = new AccountNumber();
                    newAccountNumber.setAccountNumber(accounNumber);
                    newAccountNumber.setStatus(AVAILABLE);
                    newAccountNumber.setType(PREFIX);
                    return newAccountNumber;
                }).toList();

        accountNumberRepository.saveAll(newAccountNumbers);

        return result;
    }

    public Collection<String> getUniqueAccountNumber(String prefix, Integer size) {
        Page<AccountNumber> accounts = accountNumberRepository.findAllByAccountNumberStartsWithAndStatus(prefix,
                AVAILABLE,
                PageRequest.of(0, size));

        return accounts.stream()
                .map(account -> account.getAccountNumber())
                .toList();
    }


    private Collection<String> createUniqueAccountNumber(@Nullable String prefix,
                                                         Integer size,
                                                         Integer accountLength) {
        List<AccountNumber> accounts = accountNumberRepository.findAll();

        Set<String> accountNumbers = accounts.stream()
                .map(AccountNumber::getAccountNumber)
                .collect(Collectors.toSet());

        Set<AccountNumber> availableAccounts = accounts.stream()
                .filter(account -> account.getStatus() == AVAILABLE)
                .collect(Collectors.toSet());

        Set<String> result = new HashSet<>();

//        int needSize = size - availableAccounts.size();
        int needSize = 1;
        log.info("Нужно создать " + needSize + " счетов");
        int k = 0;

        if (needSize > 0) {
            while (k < 10000) {
                StringBuffer stringBuffer = new StringBuffer((prefix == null || prefix == "") ? PREFIX : prefix);
                for (int i = 0; i < accountLength; i++) {
                    stringBuffer.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
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
}
