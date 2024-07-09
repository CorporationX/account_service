package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffRateChangeRequestDto;
import faang.school.accountservice.mapper.TariffRateChangeRequestMapper;
import faang.school.accountservice.model.TariffRateChangeRequest;
import faang.school.accountservice.publisher.TariffRateChangePublisher;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRateChangeRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TariffRateChangeRequestServiceTest {

    @Mock
    private TariffRateChangeRequestRepository tariffRateChangeRequestRepository;

    @Mock
    private TariffService tariffService;

    @Mock
    private TariffRateChangePublisher tariffRateChangePublisher;

    @Mock
    private TariffRateChangeRequestMapper mapper;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private ScheduledExecutorService scheduledExecutorService;

    @InjectMocks
    private TariffRateChangeRequestService tariffRateChangeRequestService;

    @Captor
    private ArgumentCaptor<TariffRateChangeRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Test
    public void shouldRequestTariffRateChangeNewRequest() {
        Long tariffId = 1L;
        LocalDateTime changeDate = LocalDateTime.now().plusDays(2);
        BigDecimal newRate = BigDecimal.valueOf(0.05);
        TariffRateChangeRequest request = TariffRateChangeRequest.builder()
                .tariffId(tariffId)
                .changeDate(changeDate)
                .newRate(newRate)
                .build();
        TariffRateChangeRequestDto expectedDto = new TariffRateChangeRequestDto(tariffId, newRate, changeDate);

        when(tariffRateChangeRequestRepository.findByTariffIdAndChangeDateAndStatus(eq(tariffId), eq(changeDate), any()))
                .thenReturn(Optional.empty());
        when(mapper.toDto(any(TariffRateChangeRequest.class))).thenReturn(expectedDto);

        ArgumentCaptor<TariffRateChangeRequest> requestCaptor = ArgumentCaptor.forClass(TariffRateChangeRequest.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        TariffRateChangeRequestDto result = tariffRateChangeRequestService.requestTariffRateChange(request);

        assertThat(result).isEqualTo(expectedDto);
        verify(tariffRateChangeRequestRepository, times(1)).save(requestCaptor.capture());
        TariffRateChangeRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getStatus()).isEqualTo(TariffRateChangeRequest.RequestStatus.PENDING);
        assertThat(capturedRequest.getRequestedAt()).isNotNull();
    }

    @Test
    public void shouldRequestTariffRateChangeExistingRequest() {
        Long tariffId = 1L;
        LocalDateTime changeDate = LocalDateTime.now().plusDays(2);
        BigDecimal newRate = BigDecimal.valueOf(0.05);
        TariffRateChangeRequest existingRequest = TariffRateChangeRequest.builder()
                .tariffId(tariffId)
                .changeDate(changeDate)
                .newRate(newRate)
                .status(TariffRateChangeRequest.RequestStatus.PENDING)
                .build();
        TariffRateChangeRequestDto expectedDto = new TariffRateChangeRequestDto(tariffId, newRate, changeDate);

        when(tariffRateChangeRequestRepository.findByTariffIdAndChangeDateAndStatus(eq(tariffId), eq(changeDate), any()))
                .thenReturn(Optional.of(existingRequest));
        when(mapper.toDto(existingRequest)).thenReturn(expectedDto);

        TariffRateChangeRequestDto result = tariffRateChangeRequestService.requestTariffRateChange(
                TariffRateChangeRequest.builder()
                        .tariffId(tariffId)
                        .changeDate(changeDate)
                        .newRate(newRate)
                        .build()
        );
        assertThat(result).isEqualTo(expectedDto);
        verify(tariffRateChangeRequestRepository, never()).save(any());
    }

    @Test
    public void shouldRequestTariffRateChangeInvalidChangeDate() {
        Long tariffId = 1L;
        LocalDateTime changeDate = LocalDateTime.now().minusDays(1);
        BigDecimal newRate = BigDecimal.valueOf(0.05);
        TariffRateChangeRequest request = TariffRateChangeRequest.builder()
                .tariffId(tariffId)
                .changeDate(changeDate)
                .newRate(newRate)
                .build();
        assertThrows(IllegalArgumentException.class, () -> {
            tariffRateChangeRequestService.requestTariffRateChange(request);
        });
    }
}