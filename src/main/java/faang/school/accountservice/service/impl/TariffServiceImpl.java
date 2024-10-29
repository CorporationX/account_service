package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.dto.TariffDto;
import faang.school.accountservice.model.entity.SavingsAccountRate;
import faang.school.accountservice.model.entity.Tariff;
import faang.school.accountservice.repository.SavingsAccountRateRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.TariffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final SavingsAccountRateRepository savingsAccountRateRepository;

    @Transactional
    @Override
    public TariffDto createTariff(TariffDto tariffDto) {
        Tariff tariff = tariffMapper.toEntity(tariffDto);
        tariff = tariffRepository.save(tariff);
        SavingsAccountRate savingsAccountRate = SavingsAccountRate.builder()
                .tariff(tariff)
                .rate(tariffDto.getRate())
                .build();
        savingsAccountRateRepository.save(savingsAccountRate);

        return null;

        // TODO dad
    }

    @Transactional
    @Override
    public TariffDto updateTariff(Long id, Double rate) {
        Tariff tariff = tariffRepository.findById(id).orElseGet(() -> {
            log.info("Tariff with  id {} not found", id);
            throw new EntityNotFoundException("Tariff with id " + id + " not found");
        });

        SavingsAccountRate savingsAccountRate = SavingsAccountRate.builder()
                .rate(rate)
                .tariff(tariff)
                .build();

        TariffDto tariffDto = tariffMapper.toDto(tariff);
        tariffDto.setRate(savingsAccountRateRepository.save(savingsAccountRate).getRate());
        return tariffDto;
    }
}
