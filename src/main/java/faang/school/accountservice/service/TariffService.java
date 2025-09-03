package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffHistory;
import faang.school.accountservice.entity.TariffRateHistory;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper mapper;

    public TariffDto create(TariffDto tariffDto) {
        Tariff tariff = mapper.toEntity(tariffDto);
        TariffRateHistory history = createTariffRateHistory(tariff, tariffDto.currentRate());

        tariff.setRates(List.of(history));

        tariff = tariffRepository.save(tariff);
        return mapper.toDto(tariff);
    }

    @Transactional
    public TariffDto update(TariffDto tariffDto) {
        Tariff tariff = findById(tariffDto.id());
        tariff.getRates().add(createTariffRateHistory(tariff, tariffDto.currentRate()));

        tariff.setType(tariffDto.type());

        tariff = tariffRepository.save(tariff);
        return mapper.toDto(tariff);
    }

    @Transactional(readOnly = true)
    public List<TariffDto> findAll() {
        return tariffRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public TariffHistory createTariffHistory(Account account, long tariffId) {
        return TariffHistory.builder()
                .account(account)
                .tariff(findById(tariffId))
                .build();
    }

    private TariffRateHistory createTariffRateHistory(Tariff tariff, BigDecimal rate) {
        log.debug("Setting new current rate {} for tariffId = {}", rate, tariff.getId());
        return TariffRateHistory.builder()
                .tariff(tariff)
                .rate(rate)
                .build();
    }

    private Tariff findById(long id) {
        log.debug("Finding tariff by id = {}", id);
        return tariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found by id = {}", id));
    }
}
