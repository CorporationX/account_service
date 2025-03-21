package faang.school.accountservice.controller;

import faang.school.accountservice.dto.RateChangeRequest;
import faang.school.accountservice.enums.changerate.Status;
import faang.school.accountservice.model.ScheduledRateChange;
import faang.school.accountservice.repository.ScheduledRateChangeRepository;
import faang.school.accountservice.service.KafkaRateChangeProducer;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rate-change")
public class RateChangeController {

    private final ScheduledRateChangeRepository rateChangeRepository;
    private final TariffService tariffService;
    private final KafkaRateChangeProducer kafkaProducer;

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleRateChange(@Valid @RequestBody RateChangeRequest request) {
        if (request.scheduledDate().isBefore(LocalDate.now().plusDays(1))) {
            return ResponseEntity.badRequest().body("Rate change must be scheduled at least 1 day in advance");
        }

        ScheduledRateChange newChange = ScheduledRateChange.builder()
                .tariffId(request.tariffId())
                .oldRate(request.oldRate())
                .newRate(request.newRate())
                .scheduledDate(request.scheduledDate())
                .status(Status.SCHEDULED)
                .build();

        rateChangeRepository.save(newChange);

        kafkaProducer.sendRateChangeNotification(
                request.tariffId(),
                request.oldRate().toString(),
                request.newRate().toString(),
                request.scheduledDate().toString()
        );

        return ResponseEntity.ok("Rate change successfully scheduled and notification sent");
    }
}