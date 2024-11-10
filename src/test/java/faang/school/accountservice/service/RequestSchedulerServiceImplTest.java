package faang.school.accountservice.service;

import faang.school.accountservice.config.executor.ExecutorProperties;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestSchedulerServiceImplTest {
    @InjectMocks
    private RequestSchedulerServiceImpl requestSchedulerService;
    @Mock
    private ExecutorProperties executorProperties;
    @Mock
    private RequestExecutorService requestExecutorService;
    @Mock
    private RequestRepository requestRepository;
    private Request request;
    private RequestTask requestTask;

    @BeforeEach
    void setUp() {
        // Настройка моков
        when(executorProperties.getCorePoolSize()).thenReturn(1);
        when(executorProperties.getMaxPoolSize()).thenReturn(2);
        when(executorProperties.getKeepAliveTime()).thenReturn(1000);
        when(executorProperties.getQueueCapacity()).thenReturn(10);
        request = new Request();
        request.setId(UUID.randomUUID());
        request.setStatus(RequestStatus.PENDING);
        request.setScheduledAt(LocalDateTime.now().minusMinutes(1));
        requestTask = new RequestTask();
        requestTask.setCurrentHandlerStep(1L);
        request.setRequestTasks(Arrays.asList(requestTask));
    }

    @Test
    void testInitCallsProcessScheduledRequests() {

        when(requestRepository.findAllByStatus(RequestStatus.PENDING)).thenReturn(List.of(request));

        requestSchedulerService.init();

        verify(requestExecutorService, times(1)).executeRequest(request.getId());
    }
}