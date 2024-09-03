package faang.school.accountservice.service;

import faang.school.accountservice.exception.ExceptionMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnumConverter {

    public <E extends Enum<E>> E checkEnumAndTransformation(String accountEnum, Class<E> enumClass) {
        E newAccountEnum;
        try {
            newAccountEnum = Enum.valueOf(enumClass, accountEnum);
        } catch (IllegalArgumentException e) {
            log.error(ExceptionMessages.INCORRECT_ENUM + accountEnum);
            throw new IllegalArgumentException(ExceptionMessages.INCORRECT_ENUM + accountEnum);
        }
        return newAccountEnum;
    }
}