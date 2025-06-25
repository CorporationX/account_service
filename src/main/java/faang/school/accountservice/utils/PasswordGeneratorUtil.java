package faang.school.accountservice.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class PasswordGeneratorUtil {
    public String generatePassword() {
        return RandomStringUtils.randomNumeric(12, 20);
    }
}
