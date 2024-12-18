package faang.school.accountservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "account-type-number-length")
public class AccountTypeLengthProperties {

    private int individual;
    private int legal;
    private int savings;
    private int debit;
}
