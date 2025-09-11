package faang.school.accountservice.exception;

import faang.school.accountservice.enums.AccountType;

public class SequenceAlreadyExistsException extends AccountNumberGenerationException {
    public SequenceAlreadyExistsException(AccountType type) {
        super("Sequence already exists for account type: " + type);
    }
}

