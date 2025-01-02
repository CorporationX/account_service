package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private PaymentAccountService paymentAccountService;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestService requestService;

    private String tokenStr;
    private UUID token;
    private Long paymentAccountId;
    private Map<String, Object> input;
    private CreateRequestDto createRequestDto;
    private ResponseRequestDto responseRequestDto;

    @BeforeEach
    void setUp() {
        tokenStr = "123e4567-e89b-12d3-a456-426614174008";
        token = UUID.fromString(tokenStr);
        paymentAccountId = 1L;
        input = new HashMap<>();

        createRequestDto =
                new CreateRequestDto(tokenStr, paymentAccountId, RequestType.REMITTANCE, input);

        responseRequestDto = ResponseRequestDto.builder()
                .id(1L)
                .idempotencyToken(tokenStr)
                .requestStatus(RequestStatus.TODO)
                .requestType(RequestType.REMITTANCE)
                .input(input)
                .build();
    }

    @Test
    void createRequest_WhenNewRequest_ShouldCreateAndReturnRequest() {
        Request transientRequest = new Request();
        transientRequest.setIdempotencyToken(token);

        when(paymentAccountService.existsByAccountId(paymentAccountId)).thenReturn(true);
        when(requestMapper.toRequest(createRequestDto)).thenReturn(transientRequest);
        when(requestRepository.findByIdempotencyToken(token)).thenReturn(Optional.empty());
        when(userContext.getUserId()).thenReturn(1L);
        when(requestMapper.toResponseRequestDto(transientRequest)).thenReturn(responseRequestDto);

        ResponseRequestDto result = requestService.createRequest(createRequestDto);

        verify(requestRepository).save(transientRequest);
        assertEquals(responseRequestDto, result);
        assertTrue(transientRequest.isOpen());
        assertEquals(RequestStatus.TODO, transientRequest.getRequestStatus());
        assertEquals(1L, transientRequest.getUserId());
    }

    @Test
    void createRequest_WhenRequestExists_ShouldReturnExistingRequest() {
        Request existingRequest = new Request();
        existingRequest.setIdempotencyToken(token);

        when(paymentAccountService.existsByAccountId(1L)).thenReturn(true);
        when(requestMapper.toRequest(createRequestDto)).thenReturn(existingRequest);
        when(requestRepository.findByIdempotencyToken(token)).thenReturn(Optional.of(existingRequest));
        when(requestMapper.toResponseRequestDto(existingRequest)).thenReturn(responseRequestDto);

        ResponseRequestDto result = requestService.createRequest(createRequestDto);

        verify(requestRepository, never()).save(any());
        assertEquals(responseRequestDto, result);
    }

    @Test
    void createRequest_WhenPaymentAccountNotFound_ShouldThrowException() {
        when(paymentAccountService.existsByAccountId(1L)).thenReturn(false);

        assertThrows(DataIntegrityViolationException.class,
                () -> requestService.createRequest(createRequestDto));
    }

    @Test
    void createRequest_WhenConcurrentCreation_ShouldHandleRaceCondition() {
        Request transientRequest = new Request();
        Request existingRequest = new Request();
        transientRequest.setIdempotencyToken(token);

        when(paymentAccountService.existsByAccountId(1L)).thenReturn(true);
        when(requestMapper.toRequest(createRequestDto)).thenReturn(transientRequest);
        when(requestRepository.findByIdempotencyToken(token))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingRequest));
        when(requestRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        when(requestMapper.toResponseRequestDto(existingRequest)).thenReturn(responseRequestDto);

        ResponseRequestDto result = requestService.createRequest(createRequestDto);

        assertEquals(responseRequestDto, result);
    }

    @Test
    void getRequestsToExecute_ShouldReturnRequests() {
        List<Request> expectedRequests = List.of(new Request(), new Request());
        when(requestRepository.getRequestToExecuteByType("REMITTANCE", 10))
                .thenReturn(expectedRequests);

        List<Request> result = requestService.getRequestsToExecute(RequestType.REMITTANCE, 10);

        assertEquals(expectedRequests, result);
        verify(requestRepository).getRequestToExecuteByType("REMITTANCE", 10);
    }

    @Test
    void updateRequestStatus_ShouldUpdateStatus() {
        Long requestId = 1L;
        RequestStatus newStatus = RequestStatus.IN_PROGRESS;

        requestService.updateRequestStatus(requestId, newStatus);

        verify(requestRepository).updateRequestStatusById(requestId, "IN_PROGRESS");
    }

    @Test
    void completeRequest_ShouldCompleteRequest() {
        Long requestId = 1L;

        requestService.completeRequest(requestId);

        verify(requestRepository).completeRequest(requestId);
    }
}
