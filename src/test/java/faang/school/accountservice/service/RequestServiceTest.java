package faang.school.accountservice.service;

import faang.school.accountservice.dto.request.RequestCreationDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestStatusDto;
import faang.school.accountservice.dto.request.RequestTypeDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.mapper.request.RequestMapperImpl;
import faang.school.accountservice.mapper.request.RequestUpdateMapperImpl;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.request.RequestServiceImpl;
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

import static faang.school.accountservice.messages.ErrorMessages.REQUEST_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Spy
    private RequestMapperImpl requestMapper;

    @Spy
    private RequestUpdateMapperImpl requestUpdateMapper;

    @Mock
    private RequestRepository requestRepository;

    private final UUID requestId = UUID.randomUUID();
    private RequestCreationDto requestCreation;
    private Request request;
    private RequestUpdateDto requestUpdate;

    @BeforeEach
    public void setUp() {
        requestCreation = RequestCreationDto.builder()
                .idempotencyToken(requestId)
                .userId(1L)
                .type(RequestTypeDto.TRANSFER_FUNDS)
                .build();

        requestUpdate = RequestUpdateDto.builder()
                .idempotencyToken(UUID.randomUUID())
                .isOpen(true)
                .requestStatus(RequestStatusDto.IN_PROGRESS)
                .newStatusDetails("newStatusDetails")
                .build();

        request = Request.builder()
                .idempotencyToken(requestId)
                .userId(1L)
                .build();

        ReflectionTestUtils.setField(requestService, "threadPoolSize", 2);
        requestService.init();
    }

    @Test
    public void testCreateRequest_requestAlreadyExists() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        requestService.createRequest(requestCreation);

        verify(requestRepository, times(1)).findById(requestId);
        verify(requestRepository, never()).save(any(Request.class));

    }

    @Test
    public void testCreateRequest_success() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        requestService.createRequest(requestCreation);

        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    public void testUpdateRequest_requestNotFound() {
        UUID uuid = UUID.randomUUID();
        requestUpdate.setIdempotencyToken(uuid);
        when(requestRepository.findById(uuid)).thenReturn(Optional.empty());

        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class,
                () -> requestService.updateRequest(requestUpdate)
        );

        assertEquals(REQUEST_NOT_FOUND.formatted(uuid), exception.getMessage());
    }

    @Test
    public void testUpdateRequest_success() {
        request.setIdempotencyToken(requestUpdate.getIdempotencyToken());
        when(requestRepository.findById(requestUpdate.getIdempotencyToken())).thenReturn(Optional.of(request));

        RequestResponseDto response = requestService.updateRequest(requestUpdate);

        verify(requestRepository, times(1)).findById(requestUpdate.getIdempotencyToken());
        assertEquals(requestUpdate.getRequestStatus(), response.getStatus());
        assertEquals(requestUpdate.getNewStatusDetails(), response.getStatusDetails());
    }
}
