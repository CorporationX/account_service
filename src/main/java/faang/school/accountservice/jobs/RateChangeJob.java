package faang.school.accountservice.jobs;

import faang.school.accountservice.enums.changerate.Status;
import faang.school.accountservice.model.ScheduledRateChange;
import faang.school.accountservice.repository.ScheduledRateChangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RateChangeJob {

    private final ScheduledRateChangeRepository rateChangeRepository;

    @Scheduled(cron = "0 0 10 * * ?")
    @Transactional
    public void processRateChanges() {
        LocalDate today = LocalDate.now();
        List<ScheduledRateChange> changes = rateChangeRepository.findByScheduledDateAndStatus(today, Status.SCHEDULED);

        for (ScheduledRateChange change : changes) {
            try {
                log.info("Processing rate change for tariff {}: {} -> {}", change.getTariffId(), change.getOldRate(), change.getNewRate());
                change.setStatus(Status.COMPLETED);
                rateChangeRepository.save(change);
            } catch (Exception e) {
                log.error("Error processing rate change for tariff {}: {}", change.getTariffId(), e.getMessage());
            }
        }
    }
}