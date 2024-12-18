package faang.school.accountservice.validator;

import faang.school.accountservice.entiry.AccountNumberSequence;
import org.springframework.stereotype.Component;

@Component
public class FreeAccountNumberValidator {

    public void validateNumberSequenceIsNotExceeded(AccountNumberSequence numberSequence,
                                                    int accountNumberLength,
                                                    int accountTypeIdentity) {
        if (Long.toString(numberSequence.getCurrentSequenceValue()).length() >
                Integer.toString(accountNumberLength).length() -
                        Integer.toString(accountTypeIdentity).length()) {
            throw new InternalError("Quantity numbers of sequence for accountType: " +
                    numberSequence.getAccountType() + " exceeded");
        }
    }
}
