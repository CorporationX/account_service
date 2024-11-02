package faang.school.accountservice.service.impl.cashback;

import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;
import faang.school.accountservice.model.entity.cashback.CashbackTariff;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.service.CashbackTariffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashbackTariffServiceImpl implements CashbackTariffService {
    private final CashbackTariffRepository cashbackTariffRepository;
    private final CashbackTariffMapper cashbackTariffMapper;

    @Override
    @Transactional
    public ReadCashbackTariffDto createTariff(CreateCashbackTariffDto createCashbackTariffDto) {
        CashbackTariff cashbackTariffMapperEntity = cashbackTariffMapper.toEntity(createCashbackTariffDto);
        return cashbackTariffMapper.toDto(cashbackTariffRepository.save(cashbackTariffMapperEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public ReadCashbackTariffDto getTariff(Long tariffId) {
        CashbackTariff cashbackTariff = cashbackTariffRepository.findById(tariffId).orElseThrow(() ->
                new EntityNotFoundException("Tariff id %d not found".formatted(tariffId)));
        return cashbackTariffMapper.toDto(cashbackTariff);
    }
}
