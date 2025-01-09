package faang.school.accountservice.service.cashback;

import faang.school.accountservice.dto.cashback.CashbackMappingDto;
import faang.school.accountservice.enums.MappingType;
import faang.school.accountservice.model.cashback.AbstractCashback;
import faang.school.accountservice.model.cashback.Merchant;
import faang.school.accountservice.model.cashback.MerchantCashback;
import faang.school.accountservice.model.cashback.OperationCashback;
import faang.school.accountservice.model.cashback.OperationType;
import faang.school.accountservice.repository.cashback.MerchantRepository;
import faang.school.accountservice.repository.cashback.OperationTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class CashbackMappingFactory {
    private MerchantRepository merchantRepository;
    private OperationTypeRepository operationTypeRepository;

    public AbstractCashback<?> createMapping(CashbackMappingDto dto) {
        if (dto.getMappingType().equals(MappingType.OPERATION)) {
            OperationCashback cashback = new OperationCashback();
            OperationType operationType = operationTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Operation type not found"));

            cashback.setOperationType(operationType);
            cashback.setTariffId(dto.getTariffId());
            cashback.setTypeId(dto.getTypeId());
            cashback.setPercentage(dto.getCashbackPercentage());
            return cashback;
        } else {
            MerchantCashback cashback = new MerchantCashback();
            Merchant merchant = merchantRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Merchant not found"));
            cashback.setMerchant(merchant);
            cashback.setTariffId(dto.getTariffId());
            cashback.setTypeId(dto.getTypeId());
            cashback.setPercentage(dto.getCashbackPercentage());
            return cashback;
        }
    }
}