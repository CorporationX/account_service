package faang.school.accountservice.service;

import faang.school.accountservice.entity.TariffRateHistory;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.TariffRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateService {
    private final TariffRateHistoryRepository rateHistoryRepository;

    public BigDecimal findCurrentRateByAccountId(UUID accountId) {
        log.info("Finding current rate by accountId = {}", accountId);
        return rateHistoryRepository.findCurrentRateByAccountId(accountId)
                .map(TariffRateHistory::getRate)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Current interest rate not found by account id = {}", accountId));
    }
}
