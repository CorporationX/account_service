package faang.school.accountservice.service;

import com.github.javafaker.Faker;
import faang.school.accountservice.background_worker.RequestEventsOutboxProcessor;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.enums.RequestVersion;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.ServiceUnavailableException;
import faang.school.accountservice.mapper.RequestEventMapper;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestEventService requestEventService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RequestEventsOutboxProcessor requestEventsOutboxProcessor;

    @Spy
    @SuppressWarnings("unused")
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
    @Spy
    @SuppressWarnings("unused")
    private RequestEventMapper requestEventMapper = Mappers.getMapper(RequestEventMapper.class);

    @Captor
    ArgumentCaptor<Request> requestCaptor;
    @Captor
    ArgumentCaptor<RequestEventDto> requestEventDtoCaptor;

    @InjectMocks
    private RequestServiceImpl requestService;

    private static final Faker faker = new Faker();

    @Test
    public void testCreateRequest_success_whenDataIsValid() {
        // Arrange
        var createRequestDto = getTestCreateRequestDto();
        var userId = createRequestDto.userId();

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "User", "example@mail.com"));

        // Act
        requestService.createRequest(createRequestDto);

        // Assert
        verify(userServiceClient).getUser(userId);

        verify(requestRepository).save(requestCaptor.capture());
        var savedRequest = requestCaptor.getValue();
        assertTrue(savedRequest.isOpened());
        assertEquals(RequestStatus.TODO, savedRequest.getRequestStatus());
        assertEquals(RequestVersion.V1, savedRequest.getRequestVersion());

        verify(requestEventService).create(requestEventDtoCaptor.capture());
        var createdRequestEventDto = requestEventDtoCaptor.getValue();
        assertEquals(createRequestDto.token(), createdRequestEventDto.id());
        assertEquals(userId, createdRequestEventDto.userId());
        assertEquals(createRequestDto.requestType(), createdRequestEventDto.requestType());

        verify(requestEventsOutboxProcessor).newRequestEventsAdded();
    }

    @Test
    public void testCreateRequest_throwsResourceNotFoundException_whenUserIsNotFound() {
        // Arrange
        var createRequestDto = getTestCreateRequestDto();
        var notFoundException = mock(FeignException.NotFound.class);
        when(userServiceClient.getUser(createRequestDto.userId())).thenThrow(notFoundException);

        // Act + Assert
        var exception = assertThrows(
                ResourceNotFoundException.class,
                () -> requestService.createRequest(createRequestDto)
        );

        assertEquals("User with id: %d is not found".formatted(createRequestDto.userId()), exception.getMessage());
        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testCreateRequest_throwServiceUnavailableException_whenServiceIsUnavailable() {
        // Arrange
        var createRequestDto = getTestCreateRequestDto();

        var feignException = mock(FeignException.class);
        when(feignException.getMessage()).thenReturn("Connection refused");
        when(userServiceClient.getUser(createRequestDto.userId())).thenThrow(feignException);

        // Act + Assert
        var exception = assertThrows(
                ServiceUnavailableException.class,
                () -> requestService.createRequest(createRequestDto)
        );

        assertTrue(exception.getMessage().contains("Unable to verify user existence"));
        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestStatusByToken_shouldUpdateStatusAndSaveRequest_whenStatusChanged() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .isOpened(true)
                .build();

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act
        requestService.updateRequestStatusByToken(requestToken, RequestStatus.READY);

        // Assert
        assertEquals(RequestStatus.READY, request.getRequestStatus());
        assertTrue(request.isOpened());
        verify(requestRepository).save(request);

        verify(requestEventService).create(requestEventDtoCaptor.capture());
        var createdRequestEventDto = requestEventDtoCaptor.getValue();
        assertEquals(requestToken, createdRequestEventDto.id());
        assertEquals(1L, createdRequestEventDto.userId());
        assertEquals(RequestStatus.READY, createdRequestEventDto.requestStatus());

        verify(requestEventsOutboxProcessor).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestStatusByToken_shouldCloseRequest_whenStatusChangedToDone() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .isOpened(true)
                .build();

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act
        requestService.updateRequestStatusByToken(requestToken, RequestStatus.DONE);

        // Assert
        assertEquals(RequestStatus.DONE, request.getRequestStatus());
        assertFalse(request.isOpened());
        verify(requestRepository).save(request);
    }

    @Test
    public void testUpdateRequestStatusByToken_shouldCloseRequest_whenStatusChangedToCancelled() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .isOpened(true)
                .build();

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act
        requestService.updateRequestStatusByToken(requestToken, RequestStatus.CANCELLED);

        // Assert
        assertEquals(RequestStatus.CANCELLED, request.getRequestStatus());
        assertFalse(request.isOpened());
        verify(requestRepository).save(request);
    }

    @Test
    public void testUpdateRequestStatusByToken_shouldDoNothing_whenStatusUnchanged() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .build();

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act
        requestService.updateRequestStatusByToken(requestToken, RequestStatus.TODO);

        // Assert
        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestStatusByToken_throwException_whenRequestNotFound_() {
        var requestToken = UUID.randomUUID();
        when(requestRepository.findById(requestToken)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                requestService.updateRequestStatusByToken(requestToken, RequestStatus.TODO));
    }

    @Test
    public void testUpdateRequestStatusByToken_throwException_whenRequestAlreadyClosed() {
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.DONE)
                .isOpened(false)
                .build();
        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        assertThrows(DataValidationException.class, () ->
                requestService.updateRequestStatusByToken(requestToken, RequestStatus.TODO));
    }

    @Test
    public void testUpdateRequestStatusByToken_throwException_whenRequestAlreadyDone_() {
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.DONE)
                .isOpened(true)
                .build();
        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        assertThrows(DataValidationException.class, () ->
                requestService.updateRequestStatusByToken(requestToken, RequestStatus.TODO));
    }

    @Test
    public void testUpdateRequestStatusByToken_throwException_whenRequestAlreadyCancelled() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.CANCELLED)
                .isOpened(true)
                .build();
        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        assertThrows(DataValidationException.class, () ->
                requestService.updateRequestStatusByToken(requestToken, RequestStatus.TODO));
    }

    @Test
    public void testUpdateRequestBodyByToken_shouldUpdateBodyAndSaveRequest_whenRequestExistsAndOpen() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .isOpened(true)
                .body(Map.of("oldKey", "oldValue"))
                .build();
        Map<String, Object> newBody = Map.of("key1", "value1", "key2", 123);

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act
        requestService.updateRequestBodyByToken(requestToken, newBody);

        // Assert
        assertEquals(newBody, request.getBody());
        verify(requestRepository).save(request);

        verify(requestEventService).create(requestEventDtoCaptor.capture());
        var createdRequestEventDto = requestEventDtoCaptor.getValue();
        assertEquals(requestToken, createdRequestEventDto.id());
        assertEquals(1L, createdRequestEventDto.userId());
        assertEquals(newBody, createdRequestEventDto.body());

        verify(requestEventsOutboxProcessor).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestBodyByToken_throwResourceNotFoundException_whenRequestNotFound() {
        // Arrange
        var requestToken = UUID.randomUUID();
        Map<String, Object> newBody = Map.of("key", "value");

        when(requestRepository.findById(requestToken)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () ->
                requestService.updateRequestBodyByToken(requestToken, newBody));

        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestBodyByToken_throwDataValidationException_whenRequestClosed() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .isOpened(false)
                .build();

        Map<String, Object> newBody = Map.of("key", "value");

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act + Assert
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                requestService.updateRequestBodyByToken(requestToken, newBody));

        assertTrue(exception.getMessage().contains("already closed"));
        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestBodyByToken_throwDataValidationException_whenRequestStatusIsDone() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.DONE)
                .isOpened(true)
                .build();
        Map<String, Object> newBody = Map.of("key", "value");

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act + Assert
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                requestService.updateRequestBodyByToken(requestToken, newBody));

        assertTrue(exception.getMessage().contains("already DONE"));
        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestBodyByToken_shouldThrowDataValidationException_whenRequestStatusIsCancelled() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.CANCELLED)
                .isOpened(true)
                .build();
        Map<String, Object> newBody = Map.of("key", "value");

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act + Assert
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                requestService.updateRequestBodyByToken(requestToken, newBody));

        assertTrue(exception.getMessage().contains("already CANCELLED"));
        verify(requestRepository, never()).save(any());
        verify(requestEventService, never()).create(any());
        verify(requestEventsOutboxProcessor, never()).newRequestEventsAdded();
    }

    @Test
    public void testUpdateRequestBodyByToken_shouldStillUpdateBody_whenBodyIsEmpty() {
        // Arrange
        var requestToken = UUID.randomUUID();
        var request = Request.builder()
                .token(requestToken)
                .userId(1)
                .requestType(RequestType.CREATE)
                .requestStatus(RequestStatus.TODO)
                .isOpened(true)
                .body(Map.of("oldKey", "oldValue"))
                .build();
        Map<String, Object> newBody = Collections.emptyMap();

        when(requestRepository.findById(requestToken)).thenReturn(Optional.of(request));

        // Act
        requestService.updateRequestBodyByToken(requestToken, newBody);

        // Assert
        assertEquals(newBody, request.getBody());
        verify(requestRepository).save(request);

        verify(requestEventService).create(requestEventDtoCaptor.capture());
        var createdRequestEventDto = requestEventDtoCaptor.getValue();
        assertEquals(requestToken, createdRequestEventDto.id());
        assertEquals(1L, createdRequestEventDto.userId());
        assertEquals(newBody, createdRequestEventDto.body());

        verify(requestEventsOutboxProcessor).newRequestEventsAdded();
    }

    private static CreateRequestDto getTestCreateRequestDto() {
        RequestType[] statuses = RequestType.values();
        var randomRequestType = statuses[faker.random().nextInt(statuses.length)];

        return CreateRequestDto.builder()
                .token(UUID.randomUUID())
                .userId(2L)
                .requestType(randomRequestType)
                .body(new HashMap<>())
                .build();
    }
}