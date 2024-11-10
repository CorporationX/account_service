package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.handler.RequestTaskHandler;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestExecutorServiceImplTest {
    @InjectMocks
    private RequestExecutorServiceImpl requestExecutorServiceImpl;
    @Mock
    private List<RequestTaskHandler> handlers;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestTaskRepository requestTaskRepository;
    private UUID requestId;
    private Request request;
    private List<RequestTask> requestTasks;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        request = new Request();
        request.setId(requestId);
        request.setStatus(RequestStatus.PENDING);
        requestTasks = new ArrayList<>();
    }

    @Test
    void executeRequestTest() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(requestTasks);

        requestExecutorServiceImpl.executeRequest(requestId);

        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void getStatusTest() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RequestStatus status = requestExecutorServiceImpl.getStatus(requestId);

        Assertions.assertEquals(request.getStatus(), status);
    }
}

