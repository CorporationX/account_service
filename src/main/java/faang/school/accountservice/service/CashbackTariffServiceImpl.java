package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.TypeMappingDto;
import faang.school.accountservice.entity.CashbackTariff;
import faang.school.accountservice.entity.MerchantCashback;
import faang.school.accountservice.entity.OperationCashback;
import faang.school.accountservice.entity.id.MerchantCashbackId;
import faang.school.accountservice.entity.id.OperationCashbackId;
import faang.school.accountservice.enums.MappingType;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CashbackTariffServiceImpl implements CashbackTariffService {
    private final CashbackTariffRepository cashbackTariffRepository;
    private final CashbackTariffMapper cashbackTariffMapper;
    private final MerchantCashbackRepository merchantCashbackRepository;
    private final OperationCashbackRepository operationCashbackRepository;
    private final MerchantRepository merchantRepository;
    private final OperationTypeRepository operationTypeRepository;

    @Override
    public TariffDto getTariff(long id) {
        return cashbackTariffMapper.toDto(findTariffById(id));
    }

    @Override
    public TariffDto createTariff(TariffDto tariffDto) {
        tariffDto.setCreatedAt(LocalDateTime.now());
        return cashbackTariffMapper.toDto(cashbackTariffRepository.save(cashbackTariffMapper.toEntity(tariffDto)));
    }

    @Override
    public TariffDto updateTariff(TariffDto tariffDto, long id) {
        CashbackTariff cashbackTariff = findTariffById(id);
        cashbackTariff.setName(tariffDto.getName());

        return cashbackTariffMapper.toDto(cashbackTariffRepository.save(cashbackTariff));
    }

    @Override
    public void deleteTariff(long id) {
        CashbackTariff cashbackTariff = findTariffById(id);
        cashbackTariffRepository.delete(cashbackTariff);
    }

    @Override
    public TypeMappingDto getCashbackMapping(TypeMappingDto typeMappingDto) {
        long tariffId = typeMappingDto.getTariffId();
        long typeId = typeMappingDto.getTypeId();

        if (typeMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            OperationCashback operationCashback = findOperationCashback(tariffId, typeId);
            typeMappingDto.setCashbackPercentage(operationCashback.getPercentage());
            return typeMappingDto;
        }
        MerchantCashback merchantCashback = findMerchantCashback(tariffId, typeId);
        typeMappingDto.setCashbackPercentage(merchantCashback.getPercentage());
        return typeMappingDto;
    }

    @Override
    public TypeMappingDto createCashbackMapping(TypeMappingDto typeMappingDto) {
        validateTypeMappingDto(typeMappingDto);
        long tariffId = typeMappingDto.getTariffId();
        long typeId = typeMappingDto.getTypeId();
        double percentage = typeMappingDto.getCashbackPercentage();

        if (typeMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            OperationCashback operationCashback = new OperationCashback();
            operationCashback.setTariffId(tariffId);
            operationCashback.setOperationId(typeId);
            operationCashback.setPercentage(percentage);
            operationCashbackRepository.save(operationCashback);
        } else {
            MerchantCashback merchantCashback = new MerchantCashback();
            merchantCashback.setTariffId(tariffId);
            merchantCashback.setMerchantId(typeId);
            merchantCashback.setPercentage(percentage);
            merchantCashbackRepository.save(merchantCashback);
        }
        return typeMappingDto;
    }

    @Override
    public void updateCashbackMapping(TypeMappingDto typeMappingDto) {
        validateTypeMappingDto(typeMappingDto);
        long typeId = typeMappingDto.getTypeId();
        long tariffId = typeMappingDto.getTariffId();
        double percentage = typeMappingDto.getCashbackPercentage();

        if (typeMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            OperationCashback operationCashback = findOperationCashback(typeId, tariffId);
            operationCashback.setTariffId(tariffId);
            operationCashback.setPercentage(percentage);
            operationCashbackRepository.save(operationCashback);
        } else {
            MerchantCashback merchantCashback = findMerchantCashback(typeId, tariffId);
            merchantCashback.setTariffId(tariffId);
            merchantCashback.setPercentage(percentage);
            merchantCashbackRepository.save(merchantCashback);
        }
    }

    @Override
    public void deleteCashbackMapping(TypeMappingDto typeMappingDto) {
        long tariffId = typeMappingDto.getTariffId();
        long typeId = typeMappingDto.getTypeId();

        if (typeMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            OperationCashbackId operationCashbackId = new OperationCashbackId(tariffId, typeId);
            operationCashbackRepository.deleteById(operationCashbackId);
        } else {
            MerchantCashbackId merchantCashbackId = new MerchantCashbackId(tariffId, typeId);
            merchantCashbackRepository.deleteById(merchantCashbackId);
        }
    }

    private CashbackTariff findTariffById(long id) {
       return cashbackTariffRepository.findById(id)
               .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id: " + id));
    }

    private OperationCashback findOperationCashback(long tariffId, long operationId) {
        OperationCashbackId operationCashbackId = new OperationCashbackId(tariffId, operationId);
        return operationCashbackRepository.findById(operationCashbackId)
                .orElseThrow(() -> new EntityNotFoundException("Operation cashback not found with\n tariff id: " +
                        tariffId + "\n operation id: " + operationId));
    }

    private MerchantCashback findMerchantCashback(long tariffId, long merchantId) {
        MerchantCashbackId merchantCashbackId = new MerchantCashbackId(tariffId, merchantId);
        return merchantCashbackRepository.findById(merchantCashbackId)
                .orElseThrow(() -> new EntityNotFoundException("Merchant cashback not found with\n tariff id: " +
                        tariffId + "\n operation id: " + merchantId));
    }

    private void validateTypeMappingDto(TypeMappingDto typeMappingDto) {
        if (typeMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            merchantRepository.findById(typeMappingDto.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + typeMappingDto.getTypeId()));
        }
        else {
            operationTypeRepository.findById(typeMappingDto.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Operation type not found with id: " + typeMappingDto.getTypeId()));
        }
        findTariffById(typeMappingDto.getTariffId());
    }
}
