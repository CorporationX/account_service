package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.dto.RequestUpdateContextDto;
import faang.school.accountservice.dto.RequestUpdateFlagDto;
import faang.school.accountservice.dto.RequestUpdateStatusDto;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.NotificationType;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.RequestStatus;
import faang.school.accountservice.model.RequestType;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.notification.NotificationMessageFactory;
import faang.school.accountservice.service.notification.publisher.MessagePublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private MessagePublisher messagePublisher;
    @Mock
    private NotificationMessageFactory messageFactory;
    @Mock
    private RequestMapper mapper;
    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    void setUp() {
        when(mapper.toDto(any())).thenAnswer(invocation -> {
            Request req = invocation.getArgument(0);
            return new RequestResponseDto(
                    req.getId(),
                    req.getUserId(),
                    req.getRequestType(),
                    req.getRequestStatus(),
                    req.isOpen(),
                    req.getInputRequest(),
                    req.getStatusDetails()
            );
        });
    }

    @Test
    void shouldReturnRequestResponseDto() {
        Map<String, Object> input = new HashMap<>();
        input.put("amount", 100);

        RequestCreateDto dto = new RequestCreateDto(1L, RequestType.IN_PROGRESS, "LOCK1", input);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

        RequestResponseDto response = requestService.createRequest(dto);

        verify(requestRepository).save(captor.capture());
        Request saved = captor.getValue();

        assertEquals(dto.userId(), saved.getUserId());
        assertEquals(dto.lockKey(), saved.getLockKey());
        assertEquals(dto.requestType(), saved.getRequestType());
        assertTrue(saved.isNotificationPending());
        assertEquals(NotificationType.CREATED, saved.getPendingNotificationType());

        assertNotNull(response);
        assertEquals(saved.getUserId(), response.userId());
    }

    @Test
    void shouldUpdateStatus() {
        long id = 1L;
        Request request = new Request();
        request.setId(UUID.randomUUID());
        request.setUserId(1L);
        request.setRequestStatus(RequestStatus.TO_DO);

        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RequestUpdateStatusDto statusDto = new RequestUpdateStatusDto(RequestStatus.DONE, "Completed");

        RequestResponseDto response = requestService.updateStatusRequest(id, statusDto);

        verify(requestRepository).save(request);
        assertEquals(RequestStatus.DONE, request.getRequestStatus());
        assertEquals("Completed", request.getStatusDetails());
        assertTrue(request.isNotificationPending());
        assertEquals(NotificationType.STATUS_UPDATED, request.getPendingNotificationType());
        assertEquals(request.getUserId(), response.userId());
    }

    @Test
    void shouldUpdateFlagRequest() {
        long id = 1L;
        Request request = new Request();
        request.setId(UUID.randomUUID());
        request.setOpen(true);

        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RequestUpdateFlagDto flagDto = new RequestUpdateFlagDto(false);

        RequestResponseDto response = requestService.updateFlagRequest(id, flagDto);

        verify(requestRepository).save(request);
        assertFalse(request.isOpen());
        assertTrue(request.isNotificationPending());
        assertEquals(NotificationType.FLAG_UPDATED, request.getPendingNotificationType());
        assertEquals(request.getUserId(), response.userId());
    }

    @Test
    void shouldUpdateInputRequest() {
        long id = 1L;
        Request request = new Request();
        request.setId(UUID.randomUUID());
        request.setInputRequest(new HashMap<>());

        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> newInput = new HashMap<>();
        newInput.put("key", "value");

        RequestUpdateContextDto contextDto = new RequestUpdateContextDto(newInput);

        RequestResponseDto response = requestService.updateContextRequest(id, contextDto);

        verify(requestRepository).save(request);
        assertEquals(newInput, request.getInputRequest());
        assertTrue(request.isNotificationPending());
        assertEquals(NotificationType.CONTEXT_UPDATED, request.getPendingNotificationType());
        assertEquals(request.getUserId(), response.userId());
    }
}
