package faang.school.accountservice.service.cashback;

import faang.school.accountservice.model.cashback.*;
import faang.school.accountservice.repository.cashback.MerchantCashbackRepository;
import faang.school.accountservice.repository.cashback.OperationCashbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashbackCalculationService {
    private final OperationCashbackRepository operationCashbackRepository;
    private final MerchantCashbackRepository merchantCashbackRepository;

    public BigDecimal calculateCashbackAmount(Operation operation, CashbackTariff tariff) {
        Double cashbackPercentage = findCashbackPercentage(operation, tariff);
        if (cashbackPercentage == null || cashbackPercentage == 0) {
            return BigDecimal.ZERO;
        }
        return calculateAmount(operation.getAmount(), cashbackPercentage);
    }

    private Double findCashbackPercentage(Operation operation, CashbackTariff tariff) {
        if (operation.getOperationTypeId() != null) {
            CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
            Optional<OperationCashback> operationCashback = operationCashbackRepository.findById(operationCashbackId);
            if (operationCashback.isPresent()) {
                return operationCashback.get().getPercentage();
            }
        }

        if (operation.getMerchantId() != null) {
            CashbackId merchantCashbackId = new CashbackId(tariff.getId(), operation.getMerchantId());
            return merchantCashbackRepository.findById(merchantCashbackId)
                    .map(MerchantCashback::getPercentage)
                    .orElse(0.0);
        }
        return 0.0;
    }

    private BigDecimal calculateAmount(BigDecimal operationAmount, Double percentage) {
        return operationAmount
                .multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}