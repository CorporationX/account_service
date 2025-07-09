package faang.school.accountservice.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "executor.balance-transfer")
@Component
public class BalanceTransferExecutorConfig {

    private int corePoolSize;
    private int maxPoolSize;
    private String threadNamePrefix;
}