package faang.school.accountservice.service.scheduled;

import faang.school.accountservice.dto.RateChangeRequestDto;
import faang.school.accountservice.entity.RateChangeRequest;
import faang.school.accountservice.enums.RateChangeRequestStatus;
import faang.school.accountservice.repository.RateChangeRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@EnableRetry
@Slf4j
@RequiredArgsConstructor
@Service
public class RateChangeNotificationService {
    private final RateChangeRequestRepository rateChangeRequestRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Scheduled(cron = "${scheduled.sendRateChangeApplier.cron}")
    @Transactional
    public void notifyCustomersAboutRateChanges() {

        LocalDate notificationDate = LocalDate.now().plusDays(1);

        List<RateChangeRequest> requests = rateChangeRequestRepository.findByEffectiveDateAndStatus(notificationDate, RateChangeRequestStatus.PENDING);

        requests.forEach(request -> {
            try {
                RateChangeRequestDto requestDto = RateChangeRequestDto.builder()
                        .tariffId(request.getTariff().getId())
                        .newRate(request.getNewRate())
                        .effectiveDate(request.getEffectiveDate())
                        .build();
                kafkaTemplate.send("rate-change-event", requestDto);
                request.setStatus(RateChangeRequestStatus.PROCESSING);
                rateChangeRequestRepository.save(request);

            } catch (Exception e) {
                log.error("Error sending notification to customer {}", request.getId(), e);
            }
        });
    }
}
