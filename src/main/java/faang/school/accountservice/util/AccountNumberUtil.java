package faang.school.accountservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountNumberUtil {

    public String getAccountNumber(int code, int length, long count) {

        String codeStr = String.valueOf(code);
        String countStr = String.valueOf(count);

        StringBuilder builder = new StringBuilder();
        builder.append(codeStr);

        int dif = length - codeStr.length() - countStr.length();

        if (dif < 0) {
            throw new IllegalArgumentException("count is greater than length");
        }

        builder.append("0".repeat(dif));
        builder.append(count);

        return builder.toString();
    }
}
