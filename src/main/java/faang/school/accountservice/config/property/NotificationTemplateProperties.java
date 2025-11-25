package faang.school.accountservice.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "notification.templates")
public class NotificationTemplateProperties {
    private String requestCreated;
    private String requestStatusUpdated;
    private String requestFlagUpdated;
    private String requestContextUpdated;
}
