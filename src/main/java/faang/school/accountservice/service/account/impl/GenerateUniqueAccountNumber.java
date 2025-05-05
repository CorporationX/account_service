package faang.school.accountservice.service.account.impl;

import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GenerateUniqueAccountNumber implements AccountNumberGenerator {

    private final Random random = new Random();
    private final AccountRepository accountRepository;

    //todo
    @Override
    public String generateUniqueNumber() {
        StringBuilder builder = new StringBuilder(String.valueOf(random.nextInt(1, 9)));
        IntStream.range(0, 15).forEach(num -> builder.append(random.nextInt(9)));
        if (accountRepository.existsByNumber(builder.toString())) {
            return generateUniqueNumber();
        }
        return builder.toString();
    }
}
