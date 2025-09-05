package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestServiceTest {
    @Mock
    private RequestRepository repository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void testCreateRequest() {
        UUID token = UUID.randomUUID();
        Request request = Request.builder()
                .idpToken(token)
                .userId(1L)
                .operationType(OperationType.CREATE)
                .build();

        when(repository.save(any(Request.class)))
                .thenReturn(request);

        Request created = requestService.createRequest(request);
        assertEquals(token, created.getIdpToken());
        assertEquals(RequestStatus.PENDING, created.getStatus());
    }

    @Test
    void testUpdateStatus() {
        UUID token = UUID.randomUUID();
        Request request = Request.builder()
                .idpToken(token)
                .status(RequestStatus.PENDING)
                .build();

        when(repository.findById(token))
                .thenReturn(Optional.of(request));

        requestService.updateStatus(token, RequestStatus.COMPLETED);
        verify(repository).save(request);
        assertEquals(RequestStatus.COMPLETED, request.getStatus());
    }

    @Test
    void testUpdateContext() {
        UUID token = UUID.randomUUID();
        Request request = Request.builder()
                .idpToken(token)
                .build();

        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");

        when(repository.findById(token))
                .thenReturn(Optional.of(request));

        requestService.updateContext(token, context);
        verify(repository).save(request);
        assertEquals("value", request.getInputData().get("key"));
    }
}