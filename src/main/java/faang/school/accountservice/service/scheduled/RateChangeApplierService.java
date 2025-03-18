package faang.school.accountservice.service.scheduled;


import faang.school.accountservice.entity.RateChangeRequest;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.RateChangeRequestStatus;
import faang.school.accountservice.repository.RateChangeRequestRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.TariffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RateChangeApplierService {
    private final TariffService tariffService;
    private final RateChangeRequestRepository rateChangeRequestRepository;
    private final TariffRepository tariffRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Async("rateChange")
    @Transactional
    public void applyRateChanges() {
        LocalDate effectiveDate = LocalDate.now();

        List<RateChangeRequest> requests = rateChangeRequestRepository.findByEffectiveDateAndStatus(effectiveDate,
                RateChangeRequestStatus.PROCESSING);
        log.info("Found {} rate change requests to apply", requests.size());

        for (RateChangeRequest changeRequest : requests) {
            try {
                update(changeRequest.getTariff().getId(), changeRequest.getNewRate());

            } catch (Exception e) {
                log.error("Error applying rate change for tariff {}", changeRequest.getId(), e);
                changeRequest.setStatus(RateChangeRequestStatus.FAILED);
                rateChangeRequestRepository.save(changeRequest);
            }
        }
    }
    @Transactional
    private RateChangeRequest update(UUID id, Double rate) {
        Tariff tariff = tariffRepository.findById(id).orElseThrow();
        tariff.setRate(rate);
        tariffRepository.save(tariff);

        RateChangeRequest changeRequest = rateChangeRequestRepository.findByTariffId(id).orElseThrow();
        changeRequest.setTariff(tariff);
        changeRequest.setNewRate(rate);
        changeRequest.setStatus(RateChangeRequestStatus.COMPLETED);
        changeRequest.setProcessed(true);

        log.info("Successfully applied new rate {} to tariff {}", changeRequest.getNewRate(),
                changeRequest.getId());
        return rateChangeRequestRepository.save(changeRequest);
    }
}