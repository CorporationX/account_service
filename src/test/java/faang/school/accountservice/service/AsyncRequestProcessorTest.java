package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AsyncRequestProcessorTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestServiceImpl requestService;

    @InjectMocks
    private AsyncRequestProcessor asyncRequestProcessor;

    @Test
    void testSendNotification() {
        UUID idpToken = UUID.randomUUID();
        Request request = Request.builder()
                .idpToken(idpToken)
                .status(RequestStatus.PENDING)
                .build();

        when(requestService.findById(idpToken)).thenReturn(Optional.ofNullable(request));

        Future<Void> future = asyncRequestProcessor.sendNotification(idpToken);

        verify(requestRepository).findById(idpToken);
        verify(requestService).updateStatus(idpToken, RequestStatus.PROCESSING);
        assertTrue(future.isDone());
    }

    @Test
    void testProcessPendingRequests() {
        UUID idpToken1 = UUID.randomUUID();
        UUID idpToken2 = UUID.randomUUID();

        Request request1 = Request.builder()
                .idpToken(idpToken1)
                .status(RequestStatus.PENDING)
                .build();

        Request request2 = Request.builder()
                .idpToken(idpToken2)
                .status(RequestStatus.PENDING)
                .build();

        List<Request> pendingRequests = List.of(request1, request2);

        when(requestRepository.findByStatus(RequestStatus.PENDING)).thenReturn(pendingRequests);

        asyncRequestProcessor.processPendingRequests();

        verify(requestRepository, times(2)).findById(any(UUID.class));
        verify(asyncRequestProcessor, times(2)).sendNotification(any(UUID.class));
    }
}

