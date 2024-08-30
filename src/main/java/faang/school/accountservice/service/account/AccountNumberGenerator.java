package faang.school.accountservice.service.account;

import faang.school.accountservice.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;

    @Value("${account.number.length}")
    private int accountNumberLength;

    public String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(accountNumberLength);

        for (int i = 0; i < accountNumberLength; i++) {
            accountNumber.append(random.nextInt(10));
        }

        if(accountRepository.existsByAccountNumber(accountNumber.toString())) {
            return generateAccountNumber();
        }

        return accountNumber.toString();
    }
}
