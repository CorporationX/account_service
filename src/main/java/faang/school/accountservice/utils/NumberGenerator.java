package faang.school.accountservice.utils;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Random;

@Component
public class NumberGenerator {

    public BigInteger generate(int minDigitsCount, int maxDigitsCount) {
        Random random = new Random();
        int difference = maxDigitsCount - minDigitsCount;
        int numberOfDigits = minDigitsCount + random.nextInt(difference + 1);
        return new BigInteger(numberOfDigits, random);
    }
}
