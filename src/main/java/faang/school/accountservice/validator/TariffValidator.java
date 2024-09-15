package faang.school.accountservice.validator;

import faang.school.accountservice.repository.account.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TariffValidator {

    private final TariffRepository tariffRepository;

    public void validateRateForUpdates(String rate, String lastRate) {
        try {
            int rateInt = Integer.parseInt(rate);
            if(rateInt < 0) {
                throw new RuntimeException("Rate must be a non-negative number!");
            }

            int lastRateInt = Integer.parseInt(lastRate);
            if(rateInt == lastRateInt) {
                throw new RuntimeException("The rates cannot be equal!");
            }
        } catch (NumberFormatException e) {
            String message = "Rate must contain only digits!";
            log.error(message, e);
            throw new RuntimeException(message);
        }
    }

    public void validateTariffById(UUID tariffId) {
        tariffRepository.findById(tariffId)
                .orElseThrow(() -> new RuntimeException("Tariff with id: " + tariffId +
                        " doesn't exist!"));
    }
}
