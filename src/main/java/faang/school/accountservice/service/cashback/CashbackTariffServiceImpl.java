package faang.school.accountservice.service.cashback;

import faang.school.accountservice.dto.cashback.CashbackMappingDto;
import faang.school.accountservice.dto.cashback.CashbackTariffDto;
import faang.school.accountservice.enums.MappingType;
import faang.school.accountservice.mapper.CashbackMapping;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.model.cashback.AbstractCashback;
import faang.school.accountservice.model.cashback.CashbackId;
import faang.school.accountservice.model.cashback.CashbackTariff;
import faang.school.accountservice.model.cashback.MerchantCashback;
import faang.school.accountservice.model.cashback.OperationCashback;
import faang.school.accountservice.repository.cashback.CashbackTariffRepository;
import faang.school.accountservice.repository.cashback.MerchantCashbackRepository;
import faang.school.accountservice.repository.cashback.MerchantRepository;
import faang.school.accountservice.repository.cashback.OperationCashbackRepository;
import faang.school.accountservice.repository.cashback.OperationTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CashbackTariffServiceImpl implements CashbackTariffService {
    private final CashbackTariffRepository cashbackTariffRepository;
    private final CashbackTariffMapper cashbackTariffMapper;
    private final MerchantCashbackRepository merchantCashbackRepository;
    private final OperationCashbackRepository operationCashbackRepository;
    private final MerchantRepository merchantRepository;
    private final OperationTypeRepository operationTypeRepository;
    private final CashbackMappingFactory cashbackMappingFactory;

    @Override
    @Transactional(readOnly = true)
    public CashbackTariffDto getTariff(long id) {
        return cashbackTariffMapper.toDto(findTariffById(id));
    }

    @Override
    public CashbackTariffDto createTariff(CashbackTariffDto cashbackTariffDto) {
        CashbackTariff cashbackTariff = cashbackTariffRepository
                .save(cashbackTariffMapper.toEntity(cashbackTariffDto));
        return cashbackTariffMapper.toDto(cashbackTariff);
    }

    @Override
    public CashbackTariffDto updateTariff(CashbackTariffDto cashbackTariffDto, long id) {
        CashbackTariff cashbackTariff = findTariffById(id);
        cashbackTariff.setName(cashbackTariffDto.getName());

        return cashbackTariffMapper.toDto(cashbackTariffRepository.save(cashbackTariff));
    }

    @Override
    public void deleteTariff(long id) {
        CashbackTariff cashbackTariff = findTariffById(id);
        cashbackTariffRepository.delete(cashbackTariff);
    }

    @Override
    @Transactional(readOnly = true)
    public CashbackMappingDto getCashbackMapping(CashbackMappingDto cashbackMappingDto) {
        AbstractCashback<? extends CashbackMapping> cashback = findCashback(cashbackMappingDto);

        cashbackMappingDto.setCashbackPercentage(cashback.getPercentage());
        return cashbackMappingDto;
    }

    @Override
    public CashbackMappingDto createCashbackMapping(CashbackMappingDto dto) {
        validateTypeMappingDto(dto);
            AbstractCashback<?> cashback = cashbackMappingFactory.createMapping(dto);
        if (dto.getMappingType().equals(MappingType.OPERATION)) {
            operationCashbackRepository.save((OperationCashback) cashback);
        } else {
            merchantCashbackRepository.save((MerchantCashback) cashback);
        }
        return dto;
    }

    @Override
    public void updateCashbackMapping(CashbackMappingDto cashbackMappingDto) {
        validateTypeMappingDto(cashbackMappingDto);
        long typeId = cashbackMappingDto.getTypeId();
        long tariffId = cashbackMappingDto.getTariffId();
        double percentage = cashbackMappingDto.getCashbackPercentage();

        AbstractCashback<?> cashback = findCashback(cashbackMappingDto);
        cashback.setPercentage(percentage);
        cashback.setTariffId(tariffId);
        cashback.setTypeId(typeId);

        if (cashbackMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            operationCashbackRepository.save((OperationCashback) cashback);
        } else {
            merchantCashbackRepository.save((MerchantCashback) cashback);
        }
    }

    @Override
    public void deleteCashbackMapping(CashbackMappingDto cashbackMappingDto) {
        AbstractCashback<?> cashback = findCashback(cashbackMappingDto);
        CashbackId cashbackId = new CashbackId(cashback.getTariffId(), cashback.getTypeId());

        if (cashbackMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            operationCashbackRepository.deleteById(cashbackId);
        } else {
            merchantCashbackRepository.deleteById(cashbackId);
        }
    }

    private CashbackTariff findTariffById(long id) {
        return cashbackTariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id: " + id));
    }

    private AbstractCashback<?> findCashback(CashbackMappingDto cashbackMappingDto) {
        MappingType mappingType = cashbackMappingDto.getMappingType();
        long tariffId = cashbackMappingDto.getTariffId();
        long typeId = cashbackMappingDto.getTypeId();
        CashbackId cashbackId = new CashbackId(tariffId, typeId);

        if (mappingType.equals(MappingType.OPERATION)) {
            return operationCashbackRepository.findById(cashbackId)
                    .orElseThrow(() -> new EntityNotFoundException("Merchant cashback not found with\n tariff id: " +
                            tariffId + "\n operation id: " + typeId));
        }
        return merchantCashbackRepository.findById(cashbackId)
                .orElseThrow(() -> new EntityNotFoundException("Operation cashback not found with\n tariff id: " +
                        tariffId + "\n operation id: " + typeId));
    }

    private void validateTypeMappingDto(CashbackMappingDto cashbackMappingDto) {
        if (cashbackMappingDto.getMappingType().equals(MappingType.MERCHANT)) {
            merchantRepository.findById(cashbackMappingDto.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + cashbackMappingDto.getTypeId()));
        }
        else {
            operationTypeRepository.findById(cashbackMappingDto.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Operation type not found with id: " + cashbackMappingDto.getTypeId()));
        }
        findTariffById(cashbackMappingDto.getTariffId());
    }
}