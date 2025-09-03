package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumber;
import faang.school.accountservice.repository.AccountNumberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Scheduled(fixedRate = 60000)
    public void processAccountNumber() {
        List<AccountNumber> accounts = accountNumberRepository.findAll();

        Set<String> accountNumbers = accounts.stream()
                .map(AccountNumber::getAccountNumber)
                .collect(Collectors.toSet());


        while (accounts.size() < accountNumberSize) {
            StringBuffer stringBuffer = new StringBuffer(PREFIX);
            for (int i = 0; i < ACCOUNT_LENGTH; i++) {
                stringBuffer.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
            }

            AccountNumber accountNumber = new AccountNumber();
            accountNumber.setAccountNumber(stringBuffer.toString());
            accountNumber.setType(PREFIX);

            accounts.add(accountNumber);
        }

        accountNumberRepository.saveAll(accounts);
    }
}
