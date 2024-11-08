package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.FreeAccountNumber;
import faang.school.accountservice.service.account.numbers.sequence.DigitSequenceManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class AccountNumbersManager {
    private final DigitSequenceManager digitSequenceManager;

    public void getAccountNumberAndApply(AccountNumberType type, Consumer<FreeAccountNumber> action) {
        String digitSequence = digitSequenceManager.getAndRemoveFreeAccountNumberByType(type)
                .orElseGet(() -> (createNewAccountNumber(type)));
        FreeAccountNumber accountNumberEntity = new FreeAccountNumber(type, digitSequence);
        action.accept(accountNumberEntity);
    }

    private String createNewAccountNumber(AccountNumberType type) {
        digitSequenceManager.tryStartGenerationNumberForPool(type);
        return digitSequenceManager.generateNewAccountNumberWithoutPool(type);
    }



}
