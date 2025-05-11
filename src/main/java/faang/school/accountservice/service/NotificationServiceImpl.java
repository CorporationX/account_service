package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailSender emailSender;
    private final RequestRepository requestRepository;
    private final UserServiceClient userServiceClient;

    @Value("${spring.mail.subject}")
    private String emailSubject;

    @Override
    @Async("taskExecutor")
    public void sendStatusNotification(UUID requestId) {
        Request request = requestRepository.findByIdempotencyToken(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request with id " + requestId + " doesn't exist"));
        String emailContent = String.format(
                "Your request %s is now %s",
                request.getOperationType(),
                request.getRequestStatus()
        );
        emailSender.sendEmail(
                getRecipientEmail(request.getUserId()),
                emailSubject,
                emailContent
        );
    }

    private String getRecipientEmail(Long userId) {
        return userServiceClient.getUser(userId).getEmail();
    }
}
