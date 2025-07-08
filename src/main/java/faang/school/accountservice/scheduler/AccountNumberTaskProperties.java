package faang.school.accountservice.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "scheduler.account-number")
public class AccountNumberTaskProperties {
    private String cron;
    private String lockName;
    private String lockAtLeastFor;
    private String lockAtMostFor;
    private int batchSize;
}