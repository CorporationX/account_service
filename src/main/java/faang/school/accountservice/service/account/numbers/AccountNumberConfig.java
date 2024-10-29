package faang.school.accountservice.service.account.numbers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AccountNumberConfig {

    @Value("${account-service.account-numbers.max-length-of-account-number}")
    private int maxlengthOfDigitSequence;

    @Value("${account-service.account-numbers.min-number-of-free-accounts}")
    private int minNumberOfFreeAccounts;

    @Value("${account-service.account-numbers.max-number-of-free-accounts}")
    private int maxNumberOfFreeAccounts;
}
