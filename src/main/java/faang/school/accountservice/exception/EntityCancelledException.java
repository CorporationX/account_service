package faang.school.accountservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityCancelledException extends RuntimeException {
    public EntityCancelledException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
