package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.RecipientNotFoundException;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.repository.RequestRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final static String EMAIL_CONTENT_MESSAGE = "Your request %s is now %s";

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
                EMAIL_CONTENT_MESSAGE,
                request.getOperationType(),
                request.getRequestStatus()
        );
        emailSender.sendEmail(
                getRecipientEmail(request.getUserId()),
                emailSubject,
                emailContent
        );
    }

    @Retryable(
            retryFor = {FeignException.ServiceUnavailable.class, FeignException.BadGateway.class},
            backoff = @Backoff(delay = 1000, multiplier = 2),
            noRetryFor = {RecipientNotFoundException.class}
    )
    public String getRecipientEmail(Long userId) {
        try {
            UserDto userDto = userServiceClient.getUser(userId);
            if (userDto == null) {
                throw new RecipientNotFoundException("User with ID " + userId + " not found");
            }
            return userDto.getEmail();
        } catch (FeignException.NotFound e) {
            throw new RecipientNotFoundException("User with ID " + userId + " not found");
        }
    }
}
