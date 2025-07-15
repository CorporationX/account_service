package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.dto.UpdateStatusDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.exception.ConcurrentRequestException;
import faang.school.accountservice.exception.ConflictException;
import faang.school.accountservice.kafka.producer.DataSender;
import faang.school.accountservice.kafka.producer.KafkaTopics;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository repository;
    @Mock
    private RequestMapper mapper;
    @Mock
    private EventPublisherService eventPublisherService;
    @Mock
    private KafkaTopics kafkaTopics;

    @InjectMocks
    private RequestService service;

    private final UUID key = UUID.randomUUID();
    private final String lockKey = "LK";

    private CreateRequestDto createDto;
    private Request requestEntity;
    private RequestDto requestDto;

    @BeforeEach
    void setUp(){
        createDto = CreateRequestDto.builder()
                .idempotencyKey(key)
                .userId(1L)
                .type(RequestType.PAYMENT)
                .lockKey(lockKey)
                .inputData(Map.of("foo","bar"))
                .build();

        requestEntity = new Request();
        requestEntity.setIdempotencyKey(key);
        requestEntity.setUserId(1L);
        requestEntity.setLockKey(lockKey);
        requestEntity.setInputData(createDto.getInputData());
        requestEntity.setRequestType(createDto.getType());
        requestEntity.setRequestStatus(RequestStatus.PENDING);

        requestDto = new RequestDto();
        requestDto.setIdempotencyKey(key);
        requestDto.setUserId(1L);
        requestDto.setRequestStatus(RequestStatus.PENDING);

        lenient().when(mapper.toEntity(createDto)).thenReturn(requestEntity);
        lenient().when(mapper.toDto(requestEntity)).thenReturn(requestDto);
        lenient().when(kafkaTopics.getRequestEventsTopic()).thenReturn("topic");
    }

    @Test
    void createRequest_NewRequest_SavesAndPublishesEvent() {
        when(repository.findById(key)).thenReturn(Optional.empty());
        when(repository.findByLockKeyAndIsOpenTrue(lockKey)).thenReturn(Optional.empty());
        when(repository.save(requestEntity)).thenReturn(requestEntity);

        var result = service.createRequest(createDto);

        assertEquals(requestDto, result);
        verify(repository).save(requestEntity);
        verify(eventPublisherService).publishEvent(
                eq("topic"),
                eq("request-created"),
                same(requestEntity)
        );
    }

    @Test
    void createRequest_ExistingWithSameInput_ReturnsExisting() {
        when(repository.findById(key)).thenReturn(Optional.of(requestEntity));

        var result = service.createRequest(createDto);

        assertEquals(requestDto, result);

        verify(repository, never()).save(any());
        verifyNoInteractions(eventPublisherService);
    }

    @Test
    void createRequest_ExistingWithDifferentInput_ThrowsConflict() {
        Request other = new Request();
        other.setIdempotencyKey(key);
        other.setInputData(Map.of("x","y"));
        when(repository.findById(key)).thenReturn(Optional.of(other));

        assertThrows(ConflictException.class, () -> service.createRequest(createDto));
        verify(repository, never()).save(any());
    }

    @Test
    void createRequest_LockKeyInUse_ThrowsConcurrentRequest() {
        when(repository.findById(key)).thenReturn(Optional.empty());
        when(repository.findByLockKeyAndIsOpenTrue(lockKey))
                .thenReturn(Optional.of(requestEntity));

        assertThrows(ConcurrentRequestException.class, () -> service.createRequest(createDto));
        verify(repository, never()).save(any());
    }

    @Test
    void updateStatus_Valid_SavesAndPublishesEvent() {
        UpdateStatusDto upd = new UpdateStatusDto();
        upd.setIdempotencyKey(key);
        upd.setRequestStatus(RequestStatus.COMPLETED);
        upd.setStatusDetails("done");

        when(repository.findById(key)).thenReturn(Optional.of(requestEntity));
        when(repository.save(requestEntity)).thenReturn(requestEntity);

        service.updateStatus(upd);

        assertEquals(RequestStatus.COMPLETED, requestEntity.getRequestStatus());
        assertEquals("done", requestEntity.getStatusDetails());
        verify(repository).save(requestEntity);
        verify(eventPublisherService).publishEvent(
                eq("topic"),
                eq("request-status-changed"),
                same(requestEntity)
        );
    }

    @Test
    void updateStatus_NotFound_Throws() {
        UpdateStatusDto upd = new UpdateStatusDto();
        upd.setIdempotencyKey(key);
        upd.setRequestStatus(RequestStatus.COMPLETED);
        upd.setStatusDetails("x");
        when(repository.findById(key)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.updateStatus(upd));
    }

    @Test
    void closeRequest_Valid_SavesAndPublishesEvent() {
        when(repository.findById(key)).thenReturn(Optional.of(requestEntity));
        when(repository.save(requestEntity)).thenReturn(requestEntity);

        service.closeRequest(key);

        assertFalse(requestEntity.isOpen());
        verify(repository).save(requestEntity);
        verify(eventPublisherService).publishEvent(
                eq("topic"),
                eq("request-closed"),
                same(requestEntity)
        );
    }

    @Test
    void closeRequest_NotFound_Throws() {
        when(repository.findById(key)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.closeRequest(key));
    }
}
