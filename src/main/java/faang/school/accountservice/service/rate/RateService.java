package faang.school.accountservice.service.rate;

import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.repository.rate.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;

    public Rate getRateByInterestRate(Double interestRate) {
        return rateRepository.findByInterestRate(interestRate)
                .orElseGet(() -> {
                    Rate newRate = Rate.builder()
                            .interestRate(interestRate)
                            .build();
                    return rateRepository.save(newRate);
                });
    }
}
