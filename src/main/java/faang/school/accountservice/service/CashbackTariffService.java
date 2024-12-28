package faang.school.accountservice.service;

import faang.school.accountservice.dto.cashbackdto.CashbackMappingDto;
import faang.school.accountservice.dto.cashbackdto.CashbackMappingType;
import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.model.CashbackMerchantMapping;
import faang.school.accountservice.model.CashbackOperationMapping;
import faang.school.accountservice.model.CashbackTariff;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashbackTariffService {

    private final CashbackTariffRepository tariffRepository;
    private final CashbackOperationMappingRepository operationMappingRepository;
    private final CashbackMerchantMappingRepository merchantMappingRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Long createTariff() {
        CashbackTariff tariff = new CashbackTariff();
        tariff.setCreatedAt(LocalDateTime.now());
        log.info("Create Tariff, saving to database");
        return tariffRepository.save(tariff).getId();
    }

    public CashbackTariffDto getTariffById(Long id) {
        CashbackTariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id " + id));
        log.info("Received a request to find tariff");
        return new CashbackTariffDto(
                tariff.getId(),
                tariff.getCreatedAt(),
                operationMappingRepository.findByTariffId(id),
                merchantMappingRepository.findByTariffId(id)
        );
    }

    public void addMapping(Long tariffId, CashbackMappingDto cashbackMappingDto) {
        if (cashbackMappingDto.mappingType() == CashbackMappingType.OPERATION) {
            CashbackOperationMapping operationMapping = new CashbackOperationMapping();
            operationMapping.setTariffId(tariffId);
            operationMapping.setOperationType(cashbackMappingDto.key());
            operationMapping.setCashbackPercent(cashbackMappingDto.percent());
            operationMappingRepository.save(operationMapping);
            log.info("Added operation mapping");
        } else if (cashbackMappingDto.mappingType() == CashbackMappingType.MERCHANT) {
            CashbackMerchantMapping merchantMapping = new CashbackMerchantMapping();
            merchantMapping.setTariffId(tariffId);
            merchantMapping.setMerchant(cashbackMappingDto.key());
            merchantMapping.setCashbackPercent(cashbackMappingDto.percent());
            merchantMappingRepository.save(merchantMapping);
            log.info("Added merchant mapping");
        }
    }

    public void updateMapping(Long tariffId, CashbackMappingDto cashbackMappingDto) {
        if (cashbackMappingDto.mappingType() == CashbackMappingType.OPERATION) {
            CashbackOperationMapping operationMapping = operationMappingRepository
                    .findByTariffIdAndOperationType(tariffId, cashbackMappingDto.key())
                    .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id " + tariffId));
            operationMapping.setCashbackPercent(cashbackMappingDto.percent());
            operationMappingRepository.save(operationMapping);
            log.info("Updated operation mapping");
        } else if (cashbackMappingDto.mappingType() == CashbackMappingType.MERCHANT) {
            CashbackMerchantMapping merchantMapping = merchantMappingRepository
                    .findByTariffIdAndMerchant(tariffId, cashbackMappingDto.key())
                    .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id " + tariffId));
            merchantMapping.setCashbackPercent(cashbackMappingDto.percent());
            merchantMappingRepository.save(merchantMapping);
            log.info("Updated merchant mapping");
        }
    }

    public void deleteMapping(Long tariffId, String mappingKey) {
        operationMappingRepository.deleteByTariffIdAndOperationType(tariffId, mappingKey);
        merchantMappingRepository.deleteByTariffIdAndMerchant(tariffId, mappingKey);
        log.info("Deleted mapping");
    }
}