package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.FreeAccountNumber;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class AccountNumbersManager {
    private final DigitSequenceService digitSequenceService;

    public void getAccountNumberAndApply(AccountNumberType type, Consumer<FreeAccountNumber> action) {
        String digitSequence = digitSequenceService.getAndRemoveFreeAccountNumberByType(type)
                .orElseGet(() -> (createNewAccountNumber(type)));

        FreeAccountNumber accountNumberEntity = new FreeAccountNumber(type, digitSequence);
        action.accept(accountNumberEntity);
    }

    private String createNewAccountNumber(AccountNumberType type) {
        digitSequenceService.checkForGenerationSequencesAsync(type);
        return digitSequenceService.generateNewAccountNumberWithoutPool(type);
    }
}
