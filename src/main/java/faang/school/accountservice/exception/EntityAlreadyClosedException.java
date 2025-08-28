package faang.school.accountservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityAlreadyClosedException extends RuntimeException {

    public EntityAlreadyClosedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
