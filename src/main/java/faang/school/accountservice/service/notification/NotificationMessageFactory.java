package faang.school.accountservice.service.notification;

import faang.school.accountservice.config.property.NotificationTemplateProperties;
import faang.school.accountservice.model.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageFactory {
    private final NotificationTemplateProperties templates;

    public String buildCreatedMessage(Request request) {
        return String.format(
                templates.getRequestCreated(),
                request.getId(),
                request.getRequestStatus()
        );
    }

    public String buildStatusUpdatedMessage(Request request) {
        return String.format(
                templates.getRequestStatusUpdated(),
                request.getId(),
                request.getRequestStatus()
        );
    }

    public String buildFlagUpdatedMessage(Request request) {
        return String.format(
                templates.getRequestFlagUpdated(),
                request.getId(),
                request.isOpen()
        );
    }

    public String buildContextUpdatedMessage(Request request) {
        return String.format(
                templates.getRequestContextUpdated(),
                request.getId()
        );
    }
}
