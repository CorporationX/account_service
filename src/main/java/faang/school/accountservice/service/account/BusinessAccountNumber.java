package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.account.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BusinessAccountNumber implements GeneratorAccountNumber {
    @Value("${account.type.business.number-size}")
    private int numberSize;

    @Value("${account.type.business.prefix}")
    private String prefix;

    public Type getAccountType() {
        return Type.BUSINESS;
    }

    public String generateNumber() {
        String nanoTime = String.valueOf(System.nanoTime());

        int prefixSize = prefix.length();

        String result = String.format("%s%s", prefix,
                nanoTime.substring(nanoTime.length() - (numberSize - prefixSize)));

        return result;
    }

}
