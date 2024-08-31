package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.exception.BadRequestException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Transactional
    public void createTariff(TariffDto tariffDto) {
        TariffType type = tariffDto.getType();
        if (tariffRepository.existsTariffByType(type)) {
            throw new BadRequestException("Tariff already exists with type: " + type);
        }

        List<BigDecimal> initialHistory = new ArrayList<>();
        initialHistory.add(tariffDto.getPercent());

        Tariff tariff = Tariff.builder()
                .type(type)
                .percentTariffHistory(initialHistory)
                .build();

        tariffRepository.save(tariff);
    }

    @Transactional
    public void updateTariff(TariffDto tariffDto) {
        TariffType type = tariffDto.getType();
        Tariff tariff = tariffRepository.findTariffByType(tariffDto.getType());
        if (tariff == null) {
            throw new BadRequestException("Tariff not found with type: " + type);
        }

        List<BigDecimal> percents = tariff.getPercentTariffHistory();
        percents.add(tariffDto.getPercent());

        tariff.setPercentTariffHistory(percents);
    }

    @Transactional(readOnly = true)
    public List<TariffDto> getAllTariffs() {
        List<Tariff> tariffs = tariffRepository.findAll();
        return tariffMapper.toListDto(tariffs);
    }
}
