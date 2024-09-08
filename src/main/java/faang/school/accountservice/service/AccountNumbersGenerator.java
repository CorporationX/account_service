package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumbersGenerator {

    private final AccountRepository accountRepository;

    @Value("${target_number}")
    private long targetNumbers;

    public List<String> generateAccountNumbers(long amountNumbers, AccountType accountType) {

        //из таблицы с количеством номеров  счетов каждого типа получаем количество номеров счетов
        //нужного нам типа и присваиваем это значение переменной initialAccountNumber
        long initialAccountNumber = 1L;
        List<String> numbers = generateNumbers(initialAccountNumber, amountNumbers);
        //сохраняем полученные номера в таблице free_account_numbers
        //сохраняем новое значение количества номеров счетов в таблице с количеством номеров счетов каждого типа
        return numbers;
    }

    public List<String> generateNeededAccountNumbers(AccountType accountType) {
        //из таблицы с количеством номеров  счетов каждого типа получаем количество номеров счетов
        //нужного нам типа и присваиваем это значение переменной initialAccountNumber
        long initialAccountNumber = 1L;

        if (initialAccountNumber < targetNumbers) {
            long amountNumbers = targetNumbers - initialAccountNumber;
            List<String> numbers = generateNumbers(initialAccountNumber, amountNumbers);
            //сохраняем полученные номера в таблице free_account_numbers
            //сохраняем новое значение количества номеров счетов в таблице с количеством номеров счетов каждого типа
            return numbers;
        } else {
            log.info("Генерации новых номеров счетов для {} не требуется", accountType);
            return null;
        }
    }

    private List<String> generateNumbers(long initialAccountNumber, long amountNumbers) {
        List<String> accountNumbers = new ArrayList<>();
        String accountPattern = "4200_0000_0000_0000_0000";
        long parsedAccountPattern = Long.parseLong(accountPattern);

        for (long i = initialAccountNumber; i < amountNumbers; i++) {
            long accountNumber = initialAccountNumber++;
            String newAccountNumber = String.valueOf(parsedAccountPattern + accountNumber);
            accountNumbers.add(newAccountNumber);
            log.info("Сгенерирован очередной номер счёта - {}", newAccountNumber);
        }
        return accountNumbers;
    }
}
