package faang.school.accountservice.service;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.OperationType;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.operation.OperationHandler;
import faang.school.accountservice.service.operation.OperationHandlerRegistry;
import faang.school.accountservice.service.request.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    private static final UUID IDEMPOTENCY_TOKEN = UUID.randomUUID();
    private static final Long USER_ID = 1L;
    private static final Long PROJECT_ID = 2L;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private OperationHandlerRegistry operationHandlerRegistry;

    @Mock
    private OperationHandler operationHandler;

    @Mock
    private RequestStatusPublisher requestStatusPublisher;

    @Spy
    private final RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @InjectMocks
    private RequestServiceImpl requestService;

    private CreateRequestDto createRequestDto;

    @BeforeEach
    public void setUp() {
        createRequestDto = new CreateRequestDto(
                USER_ID,
                null,
                OperationType.ACCOUNT_CREATE,
                "lock-123",
                Map.of("amount", 1000, "currency", "USD")
        );
    }

    @Test
    public void createRequest_WithValidUserRequest_RequestCreatedSuccessfully() {
        Request pendingRequest = createTestRequestUser();
        Request completedRequest = createTestRequestUser();
        completedRequest.setRequestStatus(RequestStatus.COMPLETED);
        completedRequest.setStatusDetails("Success");
        completedRequest.setIsOpen(false);

        when(requestRepository.findById(IDEMPOTENCY_TOKEN)).thenReturn(Optional.empty());
        when(requestRepository.existsByLockValueAndIsOpenTrue("lock-123")).thenReturn(false);
        when(requestRepository.save(any(Request.class))).thenReturn(completedRequest)
                .thenReturn(completedRequest);
        when(operationHandlerRegistry.getHandler(OperationType.ACCOUNT_CREATE))
                .thenReturn(operationHandler);

        ResponseRequestDto response = requestService.createRequest(IDEMPOTENCY_TOKEN, createRequestDto);

        assertNotNull(response);
        assertEquals(RequestStatus.COMPLETED, response.requestStatus());
        assertUserRequest(response);

        verify(requestRepository, times(2)).save(any(Request.class));
        verify(operationHandler).execute(any(Request.class));
        verify(requestStatusPublisher).publish(any());
    }

    @Test
    public void updateRequestStatus_ToCancelled_RequestCancelledSuccessfully() {
        Request testRequest = createTestRequestUser();
        testRequest.setRequestStatus(RequestStatus.PENDING);

        when(requestRepository.findById(IDEMPOTENCY_TOKEN)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        requestService.updateRequestStatus(
                IDEMPOTENCY_TOKEN,
                RequestStatus.CANCELLED,
                "Request cancelled by user"
        );

        assertEquals(RequestStatus.CANCELLED, testRequest.getRequestStatus());
        assertEquals(false, testRequest.getIsOpen());

        verify(requestRepository).findById(IDEMPOTENCY_TOKEN);
        verify(requestRepository).save(testRequest);
    }

    @Test
    public void updateRequestStatus_ToCompleted_RequestClosedSuccessfully() {
        Request testRequest = createTestRequestUser();
        testRequest.setRequestStatus(RequestStatus.PENDING);

        when(requestRepository.findById(IDEMPOTENCY_TOKEN)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);

        ResponseRequestDto response = requestService.updateRequestStatus(
                IDEMPOTENCY_TOKEN,
                RequestStatus.COMPLETED,
                "Operation completed"
        );

        assertEquals(RequestStatus.COMPLETED, testRequest.getRequestStatus());
        assertEquals(false, testRequest.getIsOpen());

        verify(requestRepository).findById(IDEMPOTENCY_TOKEN);
        verify(requestRepository).save(testRequest);
    }

    @Test
    public void createRequest_WithValidProjectRequest_RequestCreatedSuccessfully() {
        final Request pendingRequest = createTestRequestUser();
        Request completedRequest = createTestRequestProject();
        completedRequest.setRequestStatus(RequestStatus.COMPLETED);
        completedRequest.setStatusDetails("Success");
        completedRequest.setIsOpen(false);

        CreateRequestDto projectRequestDto = new CreateRequestDto(
                null,
                PROJECT_ID,
                OperationType.ACCOUNT_CREATE,
                "lock-123",
                Map.of("amount", 1000, "currency", "USD")
        );

        when(requestRepository.findById(IDEMPOTENCY_TOKEN)).thenReturn(Optional.empty());
        when(requestRepository.existsByLockValueAndIsOpenTrue("lock-123")).thenReturn(false);
        when(requestRepository.save(any(Request.class))).thenReturn(pendingRequest)
                .thenReturn(completedRequest);
        when(operationHandlerRegistry.getHandler(OperationType.ACCOUNT_CREATE))
                .thenReturn(operationHandler);

        ResponseRequestDto response = requestService.createRequest(IDEMPOTENCY_TOKEN, projectRequestDto);

        assertNotNull(response);
        assertProjectRequest(response);
        assertEquals(RequestStatus.COMPLETED, response.requestStatus());

        verify(requestRepository, times(2)).save(any(Request.class));
        verify(operationHandler).execute(any(Request.class));
        verify(requestStatusPublisher).publish(any());
    }

    @Test
    public void blockRequest_WhenRequestNotFound_ThrowsException() {
        when(requestRepository.findById(IDEMPOTENCY_TOKEN)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.updateRequestStatus(IDEMPOTENCY_TOKEN,
                        RequestStatus.COMPLETED, "test"));

        assertEquals(String.format("Request with idempotencyToken %s not found", IDEMPOTENCY_TOKEN),
                exception.getMessage());

        verify(requestRepository).findById(IDEMPOTENCY_TOKEN);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    public void updateRequestStatus_WhenRequestAlreadyCompleted_ThrowsException() {
        Request testRequest = createTestRequestUser();
        testRequest.setRequestStatus(RequestStatus.COMPLETED);

        when(requestRepository.findById(IDEMPOTENCY_TOKEN)).thenReturn(Optional.of(testRequest));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> requestService.updateRequestStatus(IDEMPOTENCY_TOKEN,
                        RequestStatus.FAILED, "test"));

        assertEquals(String.format("Cannot change status of final request: %s", RequestStatus.COMPLETED),
                exception.getMessage());

        verify(requestRepository).findById(IDEMPOTENCY_TOKEN);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    public void createRequest_WhenBothUserIdAndProjectId_ThrowsException() {
        CreateRequestDto invalidDto = new CreateRequestDto(
                USER_ID,
                PROJECT_ID,
                OperationType.ACCOUNT_CREATE,
                "lock-123",
                Map.of("amount", 1000)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> requestService.createRequest(IDEMPOTENCY_TOKEN, invalidDto));

        assertEquals("Must specify exactly one owner: userId or projectId", exception.getMessage());

        verifyNoInteractions(requestRepository);
    }

    @Test
    public void createRequest_WhenRequestWithTokenAlreadyExists_ReturnsExistingRequest() {
        Request existingRequest = createTestRequestUser();
        when(requestRepository.findById(IDEMPOTENCY_TOKEN))
                .thenReturn(Optional.of(existingRequest));

        ResponseRequestDto response = requestService.createRequest(IDEMPOTENCY_TOKEN, createRequestDto);

        assertNotNull(response);
        assertEquals(existingRequest.getIdempotencyToken(), response.idempotencyToken());
        assertEquals(existingRequest.getUserId(), response.userId());
        assertEquals(existingRequest.getRequestStatus(), response.requestStatus());

        verify(requestRepository).findById(IDEMPOTENCY_TOKEN);
        verify(requestRepository, never()).save(any(Request.class));
        verifyNoInteractions(operationHandlerRegistry);
    }

    private Request createTestRequestUser() {
        Request request = new Request();
        request.setIdempotencyToken(IDEMPOTENCY_TOKEN);
        request.setUserId(USER_ID);
        request.setProjectId(null);
        request.setOperationType(OperationType.ACCOUNT_CREATE);
        request.setLockValue("lock-123");
        request.setInputData(Map.of("amount", 1000));
        request.setRequestStatus(RequestStatus.PENDING);
        request.setIsOpen(true);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }

    private Request createTestRequestProject() {
        Request request = new Request();
        request.setIdempotencyToken(IDEMPOTENCY_TOKEN);
        request.setUserId(null);
        request.setProjectId(PROJECT_ID);
        request.setOperationType(OperationType.ACCOUNT_CREATE);
        request.setLockValue("lock-123");
        request.setInputData(Map.of("amount", 1000, "currency", "USD"));
        request.setRequestStatus(RequestStatus.PENDING);
        request.setIsOpen(true);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }

    private void assertUserRequest(ResponseRequestDto response) {
        assertEquals(USER_ID, response.userId());
        assertNull(response.projectId());
        assertEquals(OperationType.ACCOUNT_CREATE, response.operationType());
        assertEquals("lock-123", response.lockValue());
    }

    private void assertProjectRequest(ResponseRequestDto response) {
        assertNull(response.userId());
        assertEquals(PROJECT_ID, response.projectId());
        assertEquals(OperationType.ACCOUNT_CREATE, response.operationType());
        assertEquals("lock-123", response.lockValue());
    }
}
