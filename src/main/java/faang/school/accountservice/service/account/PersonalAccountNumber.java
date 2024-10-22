package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.account.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PersonalAccountNumber implements GeneratorAccountNumber {
    @Value("${account.type.personal.number-size}")
    private int numberSize;

    public Type getAccountType() {
        return Type.PERSONAL;
    }

    public String generateNumber() {
        String nanoTime = String.valueOf(System.nanoTime());
        String number = nanoTime.substring(nanoTime.length() - numberSize);

        return number;
    }
}
