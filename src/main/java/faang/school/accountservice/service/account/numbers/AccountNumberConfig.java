package faang.school.accountservice.service.account.numbers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AccountNumberConfig {

    @Value("${account-service.account-numbers.max-length-sequence}")
    private int maxlengthOfDigitSequence;

    @Value("${account-service.account-numbers.pool-draw-failure-exhaustion-warning-upper-limit}")
    private int poolDrawFailureExhaustionWarningUpperLimit;

    @Value("${account-service.account-numbers.batch-size}")
    private int batchSize;
}
