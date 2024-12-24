package faang.school.accountservice.validator.tariff;

import faang.school.accountservice.exception.tariff.TariffNotFoundException;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TariffValidator {
    private final TariffRepository tariffRepository;

    public Tariff validateTariffExists(Long id) {
        Optional<Tariff> tariff = tariffRepository.findById(id);
        if (tariff.isEmpty()) {
            log.error("Tariff with id {} not found", id);
            throw new TariffNotFoundException("Tariff with id: " + id + " does not exist");
        }
        return tariff.get();
    }
}