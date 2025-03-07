package faang.school.accountservice.service;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestReadDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.mapper.RequestMapperImpl;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @InjectMocks
    private RequestService requestService;
    @Mock
    private AccountService accountService;
    @Mock
    private RequestRepository requestRepository;
    @Spy
    private RequestMapperImpl requestMapper;
    @Mock
    private EntityManager entityManager;

    private Request request;
    private RequestCreateDto createDto;
    private final String IDEMPOTENT_KEY = "test-key";
    private final String AUTHOR_ACCOUNT_NUMBER = "author";
    private final String RECEIVER_ACCOUNT_NUMBER = "receiver";

    @BeforeEach
    void setUp() {
        request = Request.builder()
                .idempotentKey(IDEMPOTENT_KEY)
                .author(Account.builder().accountNumber(AUTHOR_ACCOUNT_NUMBER).build())
                .receiver(Account.builder().accountNumber(RECEIVER_ACCOUNT_NUMBER).build())
                .isOpen(true)
                .build();
        createDto = RequestCreateDto.builder()
                .idempotentKey(IDEMPOTENT_KEY)
                .authorAccountNumber(AUTHOR_ACCOUNT_NUMBER)
                .receiverAccountNumber(RECEIVER_ACCOUNT_NUMBER)
                .build();
    }

    @Test
    void testCreateRequest_RequestExist() {
        mockFindByIdempotentKey(request);

        RequestReadDto readDto = requestService.createRequest(createDto);

        verify(requestRepository, atLeastOnce()).findByIdempotentKey(IDEMPOTENT_KEY);
        assertEquals(AUTHOR_ACCOUNT_NUMBER, readDto.getAuthorAccountNumber());
    }

    @Test
    void testCreateRequest_SuccessCase() {
        ReflectionTestUtils.setField(requestService, "entityManager", entityManager);
        when(requestRepository.findByIdempotentKey(anyString())).thenReturn(Optional.empty());
        when(accountService.findByAccountNumber(AUTHOR_ACCOUNT_NUMBER)).thenReturn(request.getAuthor());
        when(accountService.findByAccountNumber(RECEIVER_ACCOUNT_NUMBER)).thenReturn(request.getReceiver());

        RequestReadDto readDto = requestService.createRequest(createDto);

        verify(requestRepository, atLeastOnce()).findByIdempotentKey(IDEMPOTENT_KEY);
        verify(entityManager, atLeastOnce()).persist(any(Request.class));
        assertEquals(AUTHOR_ACCOUNT_NUMBER, readDto.getAuthorAccountNumber());
    }

    @Test
    void testStartRequest_RequestIsNotOpen() {
        request.setOpen(false);
        mockFindByIdempotentKey(request);

        assertThrows(BusinessException.class, () -> requestService.startRequest(IDEMPOTENT_KEY));
    }

    @Test
    void testStartRequest_RequestIsLock() {
        request.setLock(true);
        mockFindByIdempotentKey(request);

        assertThrows(BusinessException.class, () -> requestService.startRequest(IDEMPOTENT_KEY));
    }

    @Test
    void testStartRequest_SuccessCase() {
        mockFindByIdempotentKey(request);
        when(requestRepository.save(request)).thenReturn(request);

        RequestReadDto dto = requestService.startRequest(IDEMPOTENT_KEY);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, atLeastOnce()).flush();
        verify(requestRepository, atLeastOnce()).save(captor.capture());
        Request capturedRequest = captor.getValue();
        assertEquals(RequestStatus.IN_PROGRESS, capturedRequest.getStatus());
        assertTrue(capturedRequest.isLock());
    }

    @Test
    void testCloseRequest_RequestIsNotOpen() {
        request.setOpen(false);
        mockFindByIdempotentKey(request);

        assertThrows(BusinessException.class, () -> requestService.closeRequest(IDEMPOTENT_KEY));
    }

    @Test
    void testCloseRequest_RequestIsNotLock() {
        request.setLock(false);
        mockFindByIdempotentKey(request);

        assertThrows(BusinessException.class, () -> requestService.closeRequest(IDEMPOTENT_KEY));
    }

    @Test
    void testCloseRequest_SuccessCase() {
        request.setLock(true);
        mockFindByIdempotentKey(request);
        when(requestRepository.save(request)).thenReturn(request);

        RequestReadDto dto = requestService.closeRequest(IDEMPOTENT_KEY);

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, atLeastOnce()).save(captor.capture());
        assertEquals(RequestStatus.COMPLETED, dto.getRequestStatus());
        assertFalse(request.isLock());
    }

    private void mockFindByIdempotentKey(Request request) {
        when(requestRepository.findByIdempotentKey(anyString())).thenReturn(Optional.of(request));
    }
}
