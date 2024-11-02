package faang.school.accountservice.service.account.numbers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AccountNumberConfig {

    @Value("${account-service.account-numbers.max-length-sequence}")
    private int maxlengthOfDigitSequence;

    @Value("${account-service.account-numbers.min-free-accounts}")
    private int minNumberOfFreeAccounts;

    @Value("${account-service.account-numbers.max-free-accounts}")
    private int maxNumberOfFreeAccounts;
}
