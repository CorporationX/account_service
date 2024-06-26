package faang.school.accountservice.service.tariff;

import faang.school.accountservice.dto.tariff.CreateTariffDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.UpdateTariffDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Rate;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.rate.RateService;
import faang.school.accountservice.validator.tariff.TariffValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final TariffValidator tariffValidator;
    private final RateService rateService;

    @Override
    @Transactional
    public TariffDto createTariff(CreateTariffDto tariffDto) {
        Tariff tariff = tariffMapper.toEntity(tariffDto);

        tariffValidator.validateCreation(tariffDto);

        Rate rate = rateService.getRate(tariffDto.getType(), tariffDto.getPercent());
        List<Rate> rateHistory = List.of(rate);
        tariff.setRateHistory(rateHistory);

        tariffRepository.save(tariff);

        return tariffMapper.toDto(tariff);
    }

    @Override
    @Transactional
    public TariffDto updateTariffRate(UpdateTariffDto tariffDto) {
        Tariff tariff = tariffRepository.findById(tariffDto.getId())
                .orElseThrow(() -> new NotFoundException("Tariff not found"));

        tariffValidator.validateUpdate(tariffDto);

        Rate rate = rateService.getRate(tariff.getType(), tariffDto.getPercent());
        tariff.getRateHistory().add(rate);

        tariffRepository.save(tariff);

        return tariffMapper.toDto(tariff);
    }

    @Override
    public TariffDto getTariff(Long id) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tariff not found"));
        return tariffMapper.toDto(tariff);
    }
}
