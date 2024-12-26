package faang.school.accountservice.service.tariff;

import faang.school.accountservice.entity.tariff.TariffRateChangelog;
import faang.school.accountservice.repository.tariff.TariffRateChangelogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TariffRateChangelogService {

    private TariffRateChangelogRepository tariffRateChangelogRepository;

    public void save(TariffRateChangelog rateChangelog) {
        tariffRateChangelogRepository.save(rateChangelog);
    }
}
