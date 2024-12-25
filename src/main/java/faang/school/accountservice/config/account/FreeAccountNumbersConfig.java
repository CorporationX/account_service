package faang.school.accountservice.config.account;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FreeAccountNumbersConfig {
    @Value("${free-account-generation.accountNumberCount}")
    private long accountNumberCount;
    @Value("${free-account-generation.accountNumberLength}")
    private int accountNumberLength;
}
