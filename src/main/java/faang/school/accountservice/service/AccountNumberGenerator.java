package faang.school.accountservice.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class AccountNumberGenerator {
    private final Random random = new Random();
    private final Set<String> generatedNumbers = new HashSet<>();

    public String generateUniqueAccountNumber() {
        String accountNumber;

        do {
            accountNumber = generateAccountNumber();
        } while (generatedNumbers.contains(accountNumber));

        generatedNumbers.add(accountNumber);
        return accountNumber;
    }


    private String generateAccountNumber() {
        int length = random.nextInt(9) + 12;
        StringBuilder accountNumber = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }
}