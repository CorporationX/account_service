package faang.school.accountservice.entity.cashback;

import faang.school.accountservice.dto.CashbackMappingDto;
import faang.school.accountservice.entity.type.CashbackMapping;
import faang.school.accountservice.enums.MappingType;
import lombok.Data;

@Data
public abstract class AbstractCashback<T extends CashbackMapping> {
    private Long tariffId;
    private Long typeId;
    private T type;
    private Double percentage;
    private Long version;

    public static AbstractCashback<?> createCashback(CashbackMappingDto cashbackMappingDto) {
        AbstractCashback<?> cashback;
        if (cashbackMappingDto.getMappingType().equals(MappingType.OPERATION)) {
            cashback = new OperationCashback();
        } else {
            cashback = new MerchantCashback();
        }
        cashback.setTariffId(cashbackMappingDto.getTariffId());
        cashback.setTypeId(cashbackMappingDto.getTypeId());
        cashback.setPercentage(cashbackMappingDto.getCashbackPercentage());
        return cashback;
    }
}
