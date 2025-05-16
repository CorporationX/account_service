package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private EmailSender emailSender;

    @Spy
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(notificationService, "emailSubject", "Request status updated");
    }

    @Test
    public void testSendStatusNotificationSuccessfully() {
        UUID requestId = UUID.randomUUID();
        Request request = Request.builder()
                .idempotencyToken(requestId)
                .userId(1L)
                .operationType(OperationType.MONEY_TRANSFER)
                .requestStatus(RequestStatus.DONE)
                .build();

        when(requestRepository.findByIdempotencyToken(requestId)).thenReturn(Optional.of(request));
        when(userServiceClient.getUser(1L)).thenReturn(UserDto.builder()
                .email("test@email.com")
                .id(1L)
                .phone("888").build());

        notificationService.sendStatusNotification(requestId);

        verify(emailSender, times(1))
                .sendEmail("test@email.com"
                        , "Request status updated"
                        , "Your request " + OperationType.MONEY_TRANSFER + " is now " + RequestStatus.DONE);
    }

    @Test
    public void testSendStatusNotificationRequestNotFound() {
        UUID requestId = UUID.randomUUID();
        when(requestRepository.findByIdempotencyToken(requestId)).thenReturn(Optional.empty());

        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class, () ->
                notificationService.sendStatusNotification(requestId));
        assertEquals("Request with id " + requestId + " doesn't exist", exception.getMessage());
    }
}
