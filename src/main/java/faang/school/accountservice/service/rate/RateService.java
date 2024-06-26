package faang.school.accountservice.service.rate;

import faang.school.accountservice.model.Rate;
import faang.school.accountservice.model.enums.TariffType;

public interface RateService {
    Rate getRate(TariffType type, float percent);
}
