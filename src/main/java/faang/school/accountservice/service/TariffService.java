package faang.school.accountservice.service;

import faang.school.accountservice.dto.tariff.HistoryDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class TariffService {
    private final TariffRepository tariffRepository;

    @Transactional
    public Tariff create(TariffRequestDto tariffDto) {
        checkExistTariff(tariffDto.name());
        Tariff tariff = Tariff.builder()
                .name(tariffDto.name())
                .rate(tariffDto.rate())
                .updatedAt(null)
                .history(new ArrayList<>())
                .build();

        return tariffRepository.save(tariff);
    }

    @Transactional
    public Tariff update(TariffUpdateDto tariffUpdate) {
        Tariff tariff = getExistingTariff(tariffUpdate.name());
        LocalDateTime now = LocalDateTime.now();

        HistoryDto historyEntry = createHistory(tariff, now);
        tariff.getHistory().add(historyEntry);

        tariff.setRate(tariffUpdate.rate());
        tariff.setUpdatedAt(now);

        return tariffRepository.save(tariff);
    }

    @Transactional(readOnly = true)
    public Tariff getTariff(TariffType tariffName) {
        return tariffRepository.findByName(tariffName)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found"));
    }

    public void checkExistTariff(TariffType name) {
        if (tariffRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Tariff with this name '%s' already exists".formatted(name));
        }
    }

    public Tariff getExistingTariff(TariffType name) {
        return tariffRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Tariff '%s' not found".formatted(name)));
    }

    private HistoryDto createHistory(Tariff tariff, LocalDateTime now) {
        LocalDateTime activeFrom = !tariff.getHistory().isEmpty() ?
                tariff.getUpdatedAt() :
                tariff.getCreatedAt();

        return HistoryDto.builder()
                .name(tariff.getName())
                .oldRate(String.format("%.2f%%", tariff.getRate()))
                .activeFrom(activeFrom)
                .activeTo(now)
                .build();
    }
}
