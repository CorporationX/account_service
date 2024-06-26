package faang.school.accountservice.service.rate;

import faang.school.accountservice.model.Rate;
import faang.school.accountservice.model.enums.TariffType;
import faang.school.accountservice.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService{

    private final RateRepository rateRepository;

    @Override
    public Rate getRate(TariffType type, float percent) {
        Optional<Rate> rate = rateRepository.findByRatePercent(percent);

        if(rate.isPresent()){
            return rate.get();

        }else {
            Rate newRate = new Rate();
            newRate.setPercent(percent);
            rateRepository.save(newRate);
            return newRate;
        }
    }

    public void updateRate(){};
}
