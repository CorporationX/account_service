package faang.school.accountservice.service.rate;

import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.repository.rate.RateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;

    public Rate getOrCreateRateByInterestRate(Double interestRate) {
        log.info("start getRateByInterestRate with interestRate: {}", interestRate);
        return rateRepository.findByInterestRate(interestRate)
                .orElseGet(() -> {
                    Rate newRate = createRate(interestRate);
                    log.info("created Rate in getRateByInterestRate: {}", newRate);

                    return rateRepository.save(newRate);
                });
    }

    private Rate createRate(Double interestRate) {
        return Rate.builder()
                .interestRate(interestRate)
                .build();
    }
}
