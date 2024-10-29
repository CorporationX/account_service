package faang.school.accountservice.config.account;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "account")
public class AccountProperties {

    private NameLength nameLength;

    @Getter
    @Setter
    public static class NameLength {
        private int min;
        private int max;
    }
}
