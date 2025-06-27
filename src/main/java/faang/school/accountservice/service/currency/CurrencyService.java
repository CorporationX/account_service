package faang.school.accountservice.service.currency;

import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.exception.currency.CurrencyNotFoundException;
import faang.school.accountservice.repository.currency.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    @Transactional(readOnly = true)
    public Currency getCurrencyById(UUID currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> {
                    log.error("Currency with {} not found", currencyId);
                    return new CurrencyNotFoundException(currencyId);
                });
    }
}
