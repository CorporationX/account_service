package faang.school.accountservice.service;

import faang.school.accountservice.exception.ExceptionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceHelper {

    public <E extends Enum<E>> E checkEnumAndTransformation(String accountEnum, Class<E> enumClass) {
        E newAccountEnum;
        try {
            newAccountEnum = Enum.valueOf(enumClass, accountEnum);
        } catch (IllegalArgumentException e) {
            log.error(ExceptionMessage.INCORRECT_ENUM + accountEnum);
            throw new IllegalArgumentException(ExceptionMessage.INCORRECT_ENUM + accountEnum);
        }
        return newAccountEnum;
    }
}