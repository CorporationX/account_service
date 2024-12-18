package faang.school.accountservice.validator;

import faang.school.accountservice.entiry.AccountNumberSequence;
import org.springframework.stereotype.Component;

@Component
public class FreeAccountNumberValidator {

    public void validateNumberSequenceIsNotExceeded(AccountNumberSequence numberSequence,
                                                    int accountNumberLength, int accountTypeIdentity) {
        if(numberSequence.getCurrentSequenceValue().toString().length() >
                Integer.toString(accountNumberLength).length() - Integer.toString(accountTypeIdentity).length()) {
            throw new InternalError("Size of sequence for accountType: " +
                    numberSequence.getAccountType() + " exceeded");
        }
    }
}
