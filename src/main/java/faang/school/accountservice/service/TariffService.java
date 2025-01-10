package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.mappers.TariffMapper;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    public TariffDto getTariff(Long id) {
        validateTariffId(id);
        Tariff tariff = tariffRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Tariff with id {} not found", id);
                throw new EntityNotFoundException("Tariff with id " + id + " not found");
            });
        return tariffMapper.toDto(tariff);
    }

    private void validateTariffExistance(Long id) {
        if (tariffRepository.existsById(id)) {
            log.error("Tariff with id {} already exists", id);
            throw new EntityExistsException("Tariff with id " + id + " already exists");
        }
    }

    private void validateTariffId(Long id) {
        if (id == null || id <= 0) {
            log.info("Received a request to get a tariff with ID NULL or LESS THAN 0");
            throw new IllegalArgumentException("ERROR: ID cannot be NULL or LESS THAN 0");
        }
    }
}
