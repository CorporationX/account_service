package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestTaskStatus;
import faang.school.accountservice.exception.RequestExecutorException;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import faang.school.accountservice.service.request.handler.RequestTaskHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.messages.ErrorMessages.REQUEST_TASK_EXECUTION_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class RequestExecutorServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestTaskRepository requestTaskRepository;
    @Mock
    private RequestTaskExecutor executor;
    @Mock
    private RequestTaskHandler handler;

    @InjectMocks
    private RequestExecutorServiceImpl service;

    private UUID requestId;
    private Request request;
    private RequestTask task;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();

        request = new Request();
        request.setIdempotencyToken(requestId);
        request.setStatus(RequestStatus.PENDING);

        task = new RequestTask();
        task.setId(UUID.randomUUID());
        task.setRequest(request);
        task.setStatus(RequestTaskStatus.PENDING);
        task.setHandler("handler-1");
        task.setCreatedAt(LocalDateTime.now());

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestTaskRepository.findByRequestIdOrderByCreatedAt(requestId))
                .thenReturn(List.of(task));
        when(handler.getHandlerId()).thenReturn("handler-1");

        service = new RequestExecutorServiceImpl(
                requestRepository,
                requestTaskRepository,
                List.of(handler),
                executor
        );
    }

    @Test
    void testExecute_SuccessfulExecution() {
        service.execute(requestId);

        verify(requestRepository, times(2)).save(any(Request.class)); // IN_PROGRESS и COMPLETED
        verify(executor, times(1)).executeTaskWithNewTx(eq(request), eq(task), eq(handler));
        assertEquals(RequestStatus.COMPLETED, request.getStatus());
    }

    @Test
    void testExecute_ThrowsRequestTaskExecutionException_OnTaskFailure() {
        doThrow(new RuntimeException("Unexpected error"))
                .when(executor).executeTaskWithNewTx(any(), any(), any());

        RequestExecutorException exception = assertThrows(
                RequestExecutorException.class,
                () -> service.execute(requestId)
        );

        assertEquals(REQUEST_TASK_EXECUTION_FAILED, exception.getMessage());

        verify(requestRepository, times(1)).save(argThat(r -> r.getStatus() == RequestStatus.IN_PROGRESS));
        verify(requestRepository, never()).save(argThat(r -> r.getStatus() == RequestStatus.COMPLETED));
    }
}
