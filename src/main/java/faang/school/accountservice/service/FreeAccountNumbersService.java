package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private long number = 1234_4123_1234_11234L;

    @Transactional
    public void getAndHandleAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = testMethod(accountType);
        numberConsumer.accept(freeAccountNumber);
    }

    public FreeAccountNumber testMethod(AccountType type) {
        return FreeAccountNumber.builder()
                .account_number(number++)
                .type(type)
                .build();
    }
}
