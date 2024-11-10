package faang.school.accountservice.service.account.numbers.sequence;

import faang.school.accountservice.enums.AccountNumberType;

import java.util.Optional;

public interface IDigitSequenceGenerator {
    Optional<String> getAndRemoveFreeAccountNumberByType(AccountNumberType type);

    String generateNewAccountNumberWithoutPool(AccountNumberType type);

    void generationSequencesAsync(AccountNumberType type);

    boolean isGenerationNeeded(AccountNumberType type);
}
