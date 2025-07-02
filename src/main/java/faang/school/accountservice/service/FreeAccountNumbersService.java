package faang.school.accountservice.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class FreeAccountNumbersService {

    private final Random RANDOM = new Random();

    public String generate() {
        int length = 12 + RANDOM.nextInt(9);
        StringBuilder sb = new StringBuilder(length);

        sb.append(1 + RANDOM.nextInt(9));

        for (int i = 1; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }
}
