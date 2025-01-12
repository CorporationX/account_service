package faang.school.accountservice.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scheduler.request")
public record SchedulerProperties(
        int period,
        int initialDelay
) {
}
