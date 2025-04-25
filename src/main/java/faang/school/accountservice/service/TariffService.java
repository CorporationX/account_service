package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffResponse;
import faang.school.accountservice.dto.TariffUpdateRequest;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffRate;
import faang.school.accountservice.exception.TariffDuplicateException;
import faang.school.accountservice.exception.TariffNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    public TariffResponse addTariff(String typeName) {
        if (tariffRepository.existsByTypeName(typeName)) {
            throw new TariffDuplicateException("Tariff %s already exists", typeName);
        }
        Tariff tariff = tariffRepository.save(createTariff(typeName));
        log.info("New tariff added: {}", tariff);
        return tariffMapper.toDto(tariff);
    }

    @Transactional
    public void updateTariff(TariffUpdateRequest request) {
        Tariff tariff = tariffRepository.findById(request.id()).orElseThrow(
                () -> new TariffNotFoundException("Tariff with id %d not found", request.id()));

        if (request.typeName() != null && !request.typeName().isBlank()) {
            tariff.setTypeName(request.typeName());
        }
        if (request.rate() != null) {
            List<TariffRate> rates = tariff.getRates();
            rates.add(createTariffRate(request.rate(), tariff));
            tariff.setRates(rates);
        }

        tariffRepository.save(tariff);
        log.info("Tariff updated: {}", tariff);
    }

    public List<TariffResponse> getAllTariffs() {
        return tariffMapper.toDtoList(tariffRepository.findAll());
    }

    private Tariff createTariff(String typeName) {
        return Tariff.builder()
                .typeName(typeName)
                .histories(new ArrayList<>())
                .build();
    }

    private TariffRate createTariffRate(BigDecimal rate, Tariff tariff) {
        return TariffRate.builder()
                .rate(rate)
                .tariff(tariff)
                .build();
    }
}
