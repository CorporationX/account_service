package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestTaskStatus;
import faang.school.accountservice.exception.RequestTaskExecutionException;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import faang.school.accountservice.service.request.handler.RequestTaskHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static faang.school.accountservice.messages.ErrorMessages.ERROR_REQUEST_TASK;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RequestTaskExecutorTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private RequestTaskHandler handler;

    @InjectMocks
    private RequestTaskExecutor executor;

    private Request request;
    private RequestTask task;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        request = new Request();
        request.setStatus(RequestStatus.IN_PROGRESS);

        task = new RequestTask();
        UUID taskId = UUID.randomUUID();
        task.setId(taskId);
        task.setStatus(RequestTaskStatus.PENDING);
    }

    @Test
    void testExecuteTaskWithNewTxSuccessfully() {
        executor.executeTaskWithNewTx(request, task, handler);

        assertEquals(RequestTaskStatus.DONE, task.getStatus());
        verify(handler).execute(request, task);
        verify(requestTaskRepository).save(task);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void testExecuteTaskWithNewTxHandlerThrowsException() {
        doThrow(new RuntimeException("Handler failed")).when(handler).execute(request, task);

        RequestTaskExecutionException exception = assertThrows(
                RequestTaskExecutionException.class,
                () -> executor.executeTaskWithNewTx(request, task, handler)
        );

        assertEquals(RequestTaskStatus.FAILED, task.getStatus());
        assertEquals(RequestStatus.FAILED, request.getStatus());
        verify(requestTaskRepository).save(task);
        verify(requestRepository).save(request);
        assertEquals(String.format(ERROR_REQUEST_TASK, task.getId()), exception.getMessage());
        assertTrue(exception.getMessage().contains("1"));
    }
}