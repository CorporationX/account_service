package faang.school.accountservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
