package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.FreeAccountNumber;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
public class AccountNumbersManager {
    private final NumbersSequenceService numbersSequenceService;

    public <T> T getAccountNumberAndApply(AccountNumberType type, Function<FreeAccountNumber, T> action) {
        String accountNumber = numbersSequenceService.getAndRemoveFreeAccountNumberByType(type)
                .orElseGet(() ->(createNewAccountNumber(type)));

        FreeAccountNumber accountNumberEntity = new FreeAccountNumber(type, accountNumber);
        return action.apply(accountNumberEntity);
    }

    private String createNewAccountNumber(AccountNumberType type) {
        numbersSequenceService.checkForGenerationSequencesAsync(type);
        return numbersSequenceService.generateAccountNumber(type);
    }
}
