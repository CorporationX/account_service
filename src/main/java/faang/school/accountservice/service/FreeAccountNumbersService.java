package faang.school.accountservice.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class FreeAccountNumbersService {

    //todo: временно решение, пока не сделана задача "Генерация уникальных номеров счетов"
    public String generateAccountNumber() {
        Random random = new Random();

        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }
}
