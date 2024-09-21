package faang.school.accountservice.service;

import faang.school.accountservice.dto.CashbackMerchantMappingDto;
import faang.school.accountservice.dto.CashbackOperationMappingDto;
import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.CashbackMerchantMapping;
import faang.school.accountservice.entity.CashbackOperationMapping;
import faang.school.accountservice.entity.CashbackTariff;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.CashbackMerchantMappingMapper;
import faang.school.accountservice.mapper.CashbackOperationMappingMapper;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.validator.CashbackMerchantMappingValidator;
import faang.school.accountservice.validator.CashbackOperationMappingValidator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CashbackTariffService {
    private final CashbackTariffRepository cashbackTariffRepository;
    private final CashbackOperationMappingRepository cashbackOperationMappingRepository;
    private final CashbackMerchantMappingRepository cashbackMerchantMappingRepository;
    private final CashbackTariffMapper cashbackTariffMapper;
    private final CashbackOperationMappingMapper cashbackOperationMappingMapper;
    private final CashbackMerchantMappingMapper cashbackMerchantMappingMapper;
    private final CashbackOperationMappingValidator cashbackOperationMappingValidator;
    private final CashbackMerchantMappingValidator cashbackMerchantMappingValidator;

    @Transactional
    public CashbackTariffDto createTariff(CashbackTariffDto cashbackTariffDto) {
        val cashbackTariff = cashbackTariffMapper.toEntity(cashbackTariffDto);
        cashbackTariffRepository.save(cashbackTariff);
        return cashbackTariffMapper.toDto(cashbackTariff);
    }

    @Transactional(readOnly = true)
    public CashbackTariffDto getTariff(Long cashbackTariffId) {
        val cashbackTariff = findCashbackTariffById(cashbackTariffId);
        return cashbackTariffMapper.toDto(cashbackTariff);
    }

    @Transactional
    public CashbackOperationMappingDto addOperationMapping(
        Long cashbackTariffId,
        CashbackOperationMappingDto mappingDto
    ) {
        val cashbackTariff = findCashbackTariffById(cashbackTariffId);
        val mapping = cashbackOperationMappingMapper.toEntity(mappingDto);
        mapping.setCashbackTariff(cashbackTariff);
        cashbackOperationMappingRepository.save(mapping);
        return cashbackOperationMappingMapper.toDto(mapping);
    }

    @Transactional
    public CashbackMerchantMappingDto addMerchantMapping(
        Long cashbackTariffId,
        CashbackMerchantMappingDto mappingDto
    ) {
        val cashbackTariff = findCashbackTariffById(cashbackTariffId);
        val mapping = cashbackMerchantMappingMapper.toEntity(mappingDto);
        mapping.setCashbackTariff(cashbackTariff);
        cashbackMerchantMappingRepository.save(mapping);
        return cashbackMerchantMappingMapper.toDto(mapping);
    }

    @Transactional(readOnly = true)
    public List<CashbackOperationMappingDto> getOperationMappings(Long cashbackTariffId) {
        return cashbackOperationMappingRepository.findByCashbackTariffId(cashbackTariffId).stream()
            .map(cashbackOperationMappingMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CashbackMerchantMappingDto> getMerchantMappings(Long cashbackTariffId) {
        return cashbackMerchantMappingRepository.findByCashbackTariffId(cashbackTariffId).stream()
            .map(cashbackMerchantMappingMapper::toDto)
            .toList();
    }

    @Transactional
    public CashbackOperationMappingDto updateOperationMapping(
        Long mappingId,
        CashbackOperationMappingDto updatedMappingDto
    ) {
        val existingMapping = findCashbackOperationMappingById(mappingId);
        cashbackOperationMappingMapper.updateEntityFromDto(updatedMappingDto, existingMapping);
        return cashbackOperationMappingMapper.toDto(existingMapping);
    }

    @Transactional
    public CashbackMerchantMappingDto updateMerchantMapping(
        Long mappingId,
        CashbackMerchantMappingDto updatedMappingDto
    ) {
        val existingMapping = findCashbackMerchantMappingById(mappingId);
        cashbackMerchantMappingMapper.updateEntityFromDto(updatedMappingDto, existingMapping);
        return cashbackMerchantMappingMapper.toDto(existingMapping);
    }

    @Transactional
    public void deleteOperationMapping(Long mappingId) {
        cashbackOperationMappingValidator.validateExists(mappingId);
        cashbackOperationMappingRepository.deleteById(mappingId);
    }

    @Transactional
    public void deleteMerchantMapping(Long mappingId) {
        cashbackMerchantMappingValidator.validateExists(mappingId);
        cashbackMerchantMappingRepository.deleteById(mappingId);
    }

    private CashbackTariff findCashbackTariffById(Long cashbackTariffId) {
        return cashbackTariffRepository.findById(cashbackTariffId)
            .orElseThrow(() -> new EntityNotFoundException("CashbackTariff with id: %d not found."
                .formatted(cashbackTariffId)));
    }

    private CashbackOperationMapping findCashbackOperationMappingById(Long cashbackMappingId) {
        return cashbackOperationMappingRepository.findById(cashbackMappingId)
            .orElseThrow(() -> new EntityNotFoundException("CashbackOperationMapping with id: %d not found."
                .formatted(cashbackMappingId)));
    }

    private CashbackMerchantMapping findCashbackMerchantMappingById(Long cashbackMappingId) {
        return cashbackMerchantMappingRepository.findById(cashbackMappingId)
            .orElseThrow(() -> new EntityNotFoundException("CashbackMerchantMapping with id: %d not found."
                .formatted(cashbackMappingId)));
    }
}