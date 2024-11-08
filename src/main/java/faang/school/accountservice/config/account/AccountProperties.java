package faang.school.accountservice.config.account;

import faang.school.accountservice.enums.account.AccountEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "account")
public class AccountProperties {

    private NameLength nameLength;
    private AccountNumbers accountNumbers;

    @Getter
    @Setter
    public static class NameLength {

        private int min;
        private int max;
    }

    @Getter
    @Setter
    public static class AccountNumbers {

        private Map<AccountEnum, Integer> needsOfFreeAccountNumbers;
        private String numberFormatter;
    }
}
