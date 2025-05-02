package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.mapper.RequestMapperImpl;
import faang.school.accountservice.publisher.EventPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.handler.RequestHandler;
import faang.school.accountservice.service.handler.RequestHandlerFactory;
import faang.school.accountservice.service.impl.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static faang.school.accountservice.enums.RequestStatus.COMPLETED;
import static faang.school.accountservice.enums.RequestStatus.TODO;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

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

    @Mock
    private UserContext userContext;

    @InjectMocks
    private RequestServiceImpl requestService;


    private RequestCreateDto requestCreateDto;
    private Request request;
    private RequestResponseDto requestResponseDto;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        requestCreateDto = new RequestCreateDto();
        requestCreateDto.setUserId(USER_ID);

        request = new Request();
        request.setUserId(requestCreateDto.getUserId());
        request.setLock(requestCreateDto.getUserId());
        request.setRequestStatus(TODO);
        request.setOpen(true);

        requestResponseDto = new RequestResponseDto();
    }

    @Test
    void testCreateRequestSuccessfully() {
        when(userContext.getUserId()).thenReturn(1L);
        when(requestMapper.toEntity(requestCreateDto)).thenReturn(request);
        when(requestRepository.save(any())).thenReturn(request);
        when(requestMapper.toDto(request)).thenReturn(requestResponseDto);

        RequestResponseDto result = requestService.createRequest(requestCreateDto);

        assertNotNull(result);
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void testCreateRequestThrowsIllegalArgumentException() {
        requestCreateDto.setUserId(2L);

        assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testCreateRequestThrowsDuplicateIdempotencyKeyException() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(requestMapper.toEntity(requestCreateDto)).thenReturn(request);
        when(requestRepository.save(request)).thenThrow(new DataIntegrityViolationException("idx_request_user_id"));

        assertThrows(DuplicateIdempotencyKeyException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testCreateRequestThrowsLockedRequestException() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(requestMapper.toEntity(requestCreateDto)).thenReturn(request);
        when(requestRepository.save(request)).thenThrow(new DataIntegrityViolationException("idx_request_lock_value_open"));

        assertThrows(LockedRequestException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testCreateRequestThrowsRuntimeException() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(requestMapper.toEntity(requestCreateDto)).thenReturn(request);
        when(requestRepository.save(request)).thenThrow(new DataIntegrityViolationException("any constraint"));

        assertThrows(RuntimeException.class, () -> requestService.createRequest(requestCreateDto));
    }

    @Test
    void testUpdateRequestSuccessfully() {
        Request request = new Request();
        request.setRequestStatus(COMPLETED);
        request.setOpen(false);

        when(requestRepository.save(request)).thenReturn(request);

        requestService.updateRequest(request);

        verify(requestRepository, times(1)).save(request);
        verify(eventPublisher, times(1)).publish(request);
    }

    @Test
    void testProcessRequestsSuccessfully() {
        Request request = new Request();
        request.setRequestStatus(TODO);

        when(requestRepository.findByRequestStatus(TODO)).thenReturn(List.of(request));
        when(requestHandlerFactory.getHandler(request.getRequestType())).thenReturn(requestHandler);

        requestService.processRequests();

        verify(requestHandler, times(1)).handle(request);
        verify(requestRepository, times(1)).save(request);
    }
}
