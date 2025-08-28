package faang.school.accountservice.exception;

import org.slf4j.helpers.MessageFormatter;

public class EntityAlreadyBlockedException extends RuntimeException {

    public EntityAlreadyBlockedException(String messagePattern, Object... argArray) {
        super(MessageFormatter.arrayFormat(messagePattern, argArray).getMessage());
    }
}
