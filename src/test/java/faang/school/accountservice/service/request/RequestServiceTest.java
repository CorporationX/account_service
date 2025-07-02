package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.mapper.request.RequestMapper;
import faang.school.accountservice.repository.request.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestServiceTest {

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRequest() {
        RequestCreateDto dto = new RequestCreateDto(1L, RequestType.TRANSFER, Map.of("amount", 100), "details");
        Request request = new Request();
        request.setUserId(1L);
        request.setLockedBy(1L);
        request.setStatus(RequestStatus.TO_DO);
        request.setActive(true);

        Request saved = new Request();
        saved.setIdempotencyToken(UUID.randomUUID());

        RequestResponseDto responseDto = new RequestResponseDto();

        when(requestMapper.toEntity(dto)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(saved);
        when(requestMapper.toDto(saved)).thenReturn(responseDto);

        RequestResponseDto result = requestService.createRequest(dto);

        verify(requestRepository).save(request);
        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void testCompleteRequest() {
        Request request = new Request();
        request.setActive(true);
        request.setStatus(RequestStatus.IN_PROGRESS);

        requestService.completeRequest(request);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.COMPLETED);
        assertThat(request.getActive()).isFalse();
        verify(requestRepository).save(request);
    }

    @Test
    void testUpdateStatus() {
        Request request = new Request();
        request.setStatus(RequestStatus.TO_DO);

        requestService.updateStatus(request, RequestStatus.CANCELLED, "cancel reason");

        assertThat(request.getStatus()).isEqualTo(RequestStatus.CANCELLED);
        assertThat(request.getDetails()).isEqualTo("cancel reason");
        verify(requestRepository).save(request);
    }

    @Test
    void testUpdateStorage() {
        Request request = new Request();
        Map<String, Object> storage = Map.of("key", "value");

        requestService.updateStorage(request, storage);

        assertThat(request.getStorage()).isEqualTo(storage);
        verify(requestRepository).save(request);
    }

    @Test
    void testClose() {
        Request request = new Request();
        request.setActive(true);

        requestService.close(request);

        assertThat(request.getActive()).isFalse();
        verify(requestRepository).save(request);
    }

    @Test
    void testGetById() {
        UUID id = UUID.randomUUID();
        Request request = new Request();
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));

        Optional<Request> result = requestService.getById(id);

        assertThat(result).contains(request);
    }

    @Test
    void testGetByStatus() {
        Request request = new Request();
        RequestResponseDto dto = new RequestResponseDto();

        when(requestRepository.findByStatus(RequestStatus.TO_DO)).thenReturn(List.of(request));
        when(requestMapper.toDto(request)).thenReturn(dto);

        List<RequestResponseDto> result = requestService.getByStatus(RequestStatus.TO_DO);

        assertThat(result).containsExactly(dto);
    }
}
