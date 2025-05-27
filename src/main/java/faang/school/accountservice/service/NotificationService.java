package faang.school.accountservice.service;

import java.util.UUID;

public interface NotificationService {

    void sendStatusNotification(UUID requestId);
}
