package faang.school.accountservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.dto.RequestInput;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.mapper.RequestMapperImpl;
import faang.school.accountservice.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private RequestMapperImpl requestMapper;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private RequestDto requestDto;
    private Request request;

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        requestDto = RequestDto.builder()
                .userId(1L)
                .operationType(OperationType.MONEY_TRANSFER)
                .requestInput(RequestInput.builder().build())
                .lockValue(1L)
                .isOpen(true)
                .requestStatus(RequestStatus.PENDING_EXECUTION)
                .build();

        request = Request.builder()
                .idempotencyToken(uuid)
                .userId(1L)
                .operationType(OperationType.MONEY_TRANSFER)
                .lockValue(1L)
                .isOpen(true)
                .requestStatus(RequestStatus.PENDING_EXECUTION)
                .inputData(Map.of("key", "value"))
                .createdAt(Instant.now())
                .statusDetails("Some details")
                .updatedAt(Instant.now())
                .version(1L)
                .build();
    }

    @Test
    public void testConvertToMapSuccessfully() {
        RequestInput input = RequestInput.builder()
                .sourceAccount("acc1")
                .targetAccount("acc2")
                .amount(BigDecimal.TEN)
                .currency(Currency.RUB)
                .build();
        Map<String, Object> result = requestService.convertToMap(input);

        assertNotNull(result);
        assertEquals("acc1", result.get("sourceAccount"));
        assertEquals("acc2", result.get("targetAccount"));
    }

    @Test
    public void testCreateRequestSuccessfully() {
        when(requestRepository.findByLockValueAndIsOpen(anyLong())).thenReturn(Collections.emptyList());
        when(requestMapper.toEntity(any(RequestDto.class))).thenReturn(request);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto result = requestService.createRequest(requestDto);

        assertNotNull(result);
        verify(requestMapper).toEntity(requestDto);
        verify(requestRepository).save(request);
    }

    @Test
    public void testUpdateStatusSuccessfully() {
        when(requestRepository.findByIdempotencyToken(any(UUID.class))).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(requestMapper.toDto(any(Request.class))).thenReturn(requestDto);

        requestDto.setRequestStatus(RequestStatus.DONE);
        requestDto.setIdempotencyToken(uuid);
        RequestDto result = requestService.updateStatus(requestDto);

        assertEquals(RequestStatus.DONE, result.getRequestStatus());
        verify(requestRepository).save(request);
    }

    @Test
    public void testUpdateIsOpenSuccessfully() {
        when(requestRepository.findByIdempotencyToken(any(UUID.class))).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(requestMapper.toDto(any(Request.class))).thenReturn(requestDto);

        requestDto.setIsOpen(false);
        requestDto.setIdempotencyToken(uuid);
        RequestDto result = requestService.updateIsOpen(requestDto);

        assertFalse(result.getIsOpen());
        verify(requestRepository).save(request);
    }

    @Test
    public void testUpdateInputDataSuccessfully() {
        when(requestRepository.findByIdempotencyToken(any())).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenReturn(request);
        when(requestMapper.toDto(any(Request.class))).thenReturn(requestDto);

        RequestInput newInput = RequestInput.builder()
                .targetAccount("acc2")
                .sourceAccount("acc1")
                .amount(BigDecimal.TEN)
                .currency(Currency.RUB)
                .build();
        requestDto.setRequestInput(newInput);
        requestDto.setIdempotencyToken(uuid);

        RequestDto result = requestService.updateInputData(requestDto);

        assertNotNull(result.getRequestInput());
        verify(requestRepository).save(request);
    }
}