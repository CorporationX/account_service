package faang.school.accountservice.service.impl.cashback;

import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.CreateTariffMerchantDto;
import faang.school.accountservice.model.cashback.CreateTariffOperationDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;
import faang.school.accountservice.model.entity.cashback.CashbackTariff;
import faang.school.accountservice.model.entity.cashback.CashbackTariffMerchant;
import faang.school.accountservice.model.entity.cashback.CashbackTariffOperationType;
import faang.school.accountservice.model.entity.cashback.Merchant;
import faang.school.accountservice.model.entity.cashback.OperationType;
import faang.school.accountservice.repository.CashbackTariffMerchantRepository;
import faang.school.accountservice.repository.CashbackTariffOperationTypeRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.repository.MerchantCashbackRepository;
import faang.school.accountservice.repository.OperationTypeCashbackRepository;
import faang.school.accountservice.service.CashbackTariffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashbackTariffServiceImpl implements CashbackTariffService {
    private final CashbackTariffRepository cashbackTariffRepository;
    private final CashbackTariffMapper cashbackTariffMapper;
    private final OperationTypeCashbackRepository operationTypeCashbackRepository;
    private final MerchantCashbackRepository merchantCashbackRepository;
    private final CashbackTariffOperationTypeRepository operationTypeCashbackOperationTypeRepository;
    private final CashbackTariffMerchantRepository cashbackTariffMerchantRepository;

    @Override
    @Transactional
    public ReadCashbackTariffDto createTariff(CreateCashbackTariffDto createCashbackTariffDto) {
        CashbackTariff cashbackTariffMapperEntity = cashbackTariffMapper.toEntity(createCashbackTariffDto);
        CashbackTariff savedTariff = cashbackTariffRepository.save(cashbackTariffMapperEntity);
        addOperations(savedTariff, createCashbackTariffDto.operations());
        addMerchants(savedTariff, createCashbackTariffDto.merchants());
        return cashbackTariffMapper.toDto(savedTariff);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadCashbackTariffDto getTariff(Long tariffId) {
        CashbackTariff cashbackTariff = cashbackTariffRepository.findById(tariffId).orElseThrow(() ->
                new EntityNotFoundException("Tariff with id %d not found".formatted(tariffId)));
        return cashbackTariffMapper.toDto(cashbackTariff);
    }

    private void addOperations(CashbackTariff tariff, List<CreateTariffOperationDto> operationsDto) {
        var operations = new ArrayList<CashbackTariffOperationType>();
        operationsDto.forEach(operation -> {
            OperationType foundOperation = operationTypeCashbackRepository.findById(operation.id())
                    .orElseThrow(() -> new EntityNotFoundException("Operation type with id %d not found".formatted(operation.id())));
            CashbackTariffOperationType tariffOperation = CashbackTariffOperationType.builder()
                    .cashbackTariff(tariff)
                    .operationType(foundOperation)
                    .percentage(operation.percentage())
                    .build();
            operations.add(tariffOperation);
            operationTypeCashbackOperationTypeRepository.save(tariffOperation);
        });
        tariff.setOperationTypes(operations);
    }

    private void addMerchants(CashbackTariff tariff, List<CreateTariffMerchantDto> merchantsDto) {
        var merchants = new ArrayList<CashbackTariffMerchant>();
        merchantsDto.forEach(merchant -> {
            Merchant foundMerchant = merchantCashbackRepository.findById(merchant.id())
                    .orElseThrow(() -> new EntityNotFoundException("Merchant with id %d not found".formatted(merchant.id())));
            CashbackTariffMerchant tariffMerchant = CashbackTariffMerchant.builder()
                    .tariff(tariff)
                    .merchant(foundMerchant)
                    .percentage(merchant.percentage())
                    .build();
            merchants.add(tariffMerchant);
            cashbackTariffMerchantRepository.save(tariffMerchant);
        });
        tariff.setCashbackTariffMerchants(merchants);
    }
}
