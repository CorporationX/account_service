package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.RequestCreateDto;
import faang.school.accountservice.dto.RequestGetDto;
import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.mapper.RequestMapperImpl;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.publisher.EventPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.request.handler.RequestHandler;
import faang.school.accountservice.service.request.handler.RequestHandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static faang.school.accountservice.enums.RequestStatus.COMPLETED;
import static faang.school.accountservice.enums.RequestStatus.TODO;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private RequestMapperImpl requestMapper;

    @Mock
    private RequestHandlerFactory requestHandlerFactory;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private RequestHandler requestHandler;

    @InjectMocks
    private RequestService requestService;


    private RequestCreateDto requestCreateDto;
    private Request request;
    private RequestGetDto requestGetDto;
    private final Long USER_ID = 1L;
    private final UUID TOKEN = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        requestCreateDto = new RequestCreateDto();
        requestCreateDto.setUserId(USER_ID);

        request = new Request();
        request.setUserId(requestCreateDto.getUserId());
        request.setLock(requestCreateDto.getUserId());
        request.setRequestStatus(TODO);
        request.setOpen(true);
    }

    @Test
    void testCreateRequest_Success() {
        when(requestRepository.save(request)).thenReturn(request);


        RequestGetDto result = requestService.createRequest(requestCreateDto);

        assertNotNull(result);
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void testCreateRequest_DuplicateIdempotencyKeyException() {
        when(requestRepository.save(request)).thenThrow(new DataIntegrityViolationException("idx_request_user_id"));

        assertThrows(DuplicateIdempotencyKeyException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testCreateRequest_LockedRequestException() {
        when(requestRepository.save(request)).thenThrow(new DataIntegrityViolationException("idx_request_lock_value_open"));

        assertThrows(LockedRequestException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testCreateRequest_ThrowRuntimeException() {
        when(requestRepository.save(request)).thenThrow(new DataIntegrityViolationException("any constraint"));

        assertThrows(RuntimeException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testUpdateRequest_Success() {
        Request request = new Request();
        request.setRequestStatus(COMPLETED);
        request.setOpen(false);

        when(requestRepository.save(request)).thenReturn(request);

        requestService.updateRequest(request);

        verify(requestRepository, times(1)).save(request);
        verify(eventPublisher, times(1)).publish(request);
    }

    @Test
    void testProcessRequests() {
        Request request = new Request();
        request.setRequestStatus(TODO);

        when(requestRepository.findByRequestStatus(TODO)).thenReturn(List.of(request));
        when(requestHandlerFactory.getHandler(request.getRequestType())).thenReturn(requestHandler);

        requestService.processRequests();

        verify(requestHandler, times(1)).handle(request);
        verify(requestRepository, times(1)).save(request);
    }
}