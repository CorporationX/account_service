package faang.school.accountservice.service.account.numbers;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountNumberType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class AccountNumbersManager {
    private final NumbersSequenceService numbersSequenceService;

    @Transactional
    public void getAccountNumberAndExecute(AccountNumberType accountNumberType, Consumer<FreeAccountNumber> action) {
        String accountNumber = getAndRemoveAccountNumberFromPool(accountNumberType);
        FreeAccountNumberId id = FreeAccountNumberId.builder()
                .accountNumber(accountNumber)
                .type(accountNumberType)
                .build();
        FreeAccountNumber accountNumberEntity = new FreeAccountNumber(id);
        action.accept(accountNumberEntity);
    }

    @Transactional
    public String getAndRemoveAccountNumberFromPool(AccountNumberType type) {
        return numbersSequenceService.getFreeAccountNumberByType(type)
                .orElse(createNewAccountNumber(type));
    }

    public String createNewAccountNumber(AccountNumberType type) {
        numbersSequenceService.checkForGenerationSequencesAsync(type);
        return numbersSequenceService.generateAccountNumber(type);
    }


}
