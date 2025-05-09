package faang.school.accountservice.component;

import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {
    public String generate(String prefix, int totalLength, long sequenceValue) {
        int numberLength = totalLength - prefix.length();
        String number = String.format("%0" + numberLength + "d", sequenceValue);
        return prefix + number;
    }
}
