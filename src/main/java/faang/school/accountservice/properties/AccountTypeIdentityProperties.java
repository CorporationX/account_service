package faang.school.accountservice.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "account-type-number-identity")
public class AccountTypeIdentityProperties {

    private int individual;
    private int legal;
    private int savings;
    private int debit;
}