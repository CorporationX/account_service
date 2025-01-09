package faang.school.accountservice.service.cashback;

import faang.school.accountservice.model.cashback.CashbackId;
import faang.school.accountservice.model.cashback.CashbackTariff;
import faang.school.accountservice.model.cashback.MerchantCashback;
import faang.school.accountservice.model.cashback.Operation;
import faang.school.accountservice.model.cashback.OperationCashback;
import faang.school.accountservice.repository.cashback.MerchantCashbackRepository;
import faang.school.accountservice.repository.cashback.OperationCashbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashbackCalculationServiceTest {

    @Mock
    private OperationCashbackRepository operationCashbackRepository;

    @Mock
    private MerchantCashbackRepository merchantCashbackRepository;

    @InjectMocks
    private CashbackCalculationService cashbackCalculationService;

    private Operation operation;
    private CashbackTariff tariff;
    private OperationCashback operationCashback;
    private MerchantCashback merchantCashback;

    @BeforeEach
    void setUp() {
        operation = new Operation();
        operation.setAmount(BigDecimal.valueOf(1000));

        tariff = new CashbackTariff();
        tariff.setId(1L);

        operationCashback = new OperationCashback();
        operationCashback.setPercentage(5.0);

        merchantCashback = new MerchantCashback();
        merchantCashback.setPercentage(3.0);
    }

    @Test
    @DisplayName("Should calculate cashback by operation type when available")
    void shouldCalculateCashbackByOperationType() {
        operation.setOperationTypeId(1L);
        CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
        when(operationCashbackRepository.findById(operationCashbackId))
                .thenReturn(Optional.of(operationCashback));
        BigDecimal result = cashbackCalculationService.calculateCashbackAmount(operation, tariff);
        verify(operationCashbackRepository).findById(operationCashbackId);
        verify(merchantCashbackRepository, never()).findById(any());
        assertEquals(new BigDecimal("50.00").setScale(2), result.setScale(2));
    }

    @Test
    @DisplayName("Should calculate cashback by merchant when operation type not found")
    void shouldCalculateCashbackByMerchant() {
        operation.setOperationTypeId(1L);
        operation.setMerchantId(2L);
        CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
        CashbackId merchantCashbackId = new CashbackId(tariff.getId(), operation.getMerchantId());
        when(operationCashbackRepository.findById(operationCashbackId))
                .thenReturn(Optional.empty());
        when(merchantCashbackRepository.findById(merchantCashbackId))
                .thenReturn(Optional.of(merchantCashback));
        BigDecimal result = cashbackCalculationService.calculateCashbackAmount(operation, tariff);
                verify(operationCashbackRepository).findById(operationCashbackId);
        verify(merchantCashbackRepository).findById(merchantCashbackId);
        assertEquals(new BigDecimal("30.00").setScale(2), result.setScale(2));
    }

    @Test
    @DisplayName("Should return zero when no cashback rules found")
    void shouldReturnZeroWhenNoCashbackRules() {
        operation.setOperationTypeId(1L);
        operation.setMerchantId(2L);
        CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
        CashbackId merchantCashbackId = new CashbackId(tariff.getId(), operation.getMerchantId());
        when(operationCashbackRepository.findById(operationCashbackId))
                .thenReturn(Optional.empty());
        when(merchantCashbackRepository.findById(merchantCashbackId))
                .thenReturn(Optional.empty());
        BigDecimal result = cashbackCalculationService.calculateCashbackAmount(operation, tariff);
        verify(operationCashbackRepository).findById(operationCashbackId);
        verify(merchantCashbackRepository).findById(merchantCashbackId);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should return zero for zero percentage")
    void shouldReturnZeroForZeroPercentage() {
        operation.setOperationTypeId(1L);
        operationCashback.setPercentage(0.0);
        CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
        when(operationCashbackRepository.findById(operationCashbackId))
                .thenReturn(Optional.of(operationCashback));
        BigDecimal result = cashbackCalculationService.calculateCashbackAmount(operation, tariff);
        verify(operationCashbackRepository).findById(operationCashbackId);
        verify(merchantCashbackRepository, never()).findById(any());
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should handle null ids correctly")
    void shouldHandleNullIds() {
        operation.setOperationTypeId(null);
        operation.setMerchantId(null);
        BigDecimal result = cashbackCalculationService.calculateCashbackAmount(operation, tariff);
        verify(operationCashbackRepository, never()).findById(any());
        verify(merchantCashbackRepository, never()).findById(any());
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should round cashback amount correctly")
    void shouldRoundCashbackAmountCorrectly() {
        operation.setAmount(BigDecimal.valueOf(99.99));
        operation.setOperationTypeId(1L);
        operationCashback.setPercentage(5.0);
        CashbackId operationCashbackId = new CashbackId(tariff.getId(), operation.getOperationTypeId());
        when(operationCashbackRepository.findById(operationCashbackId))
                .thenReturn(Optional.of(operationCashback));
        BigDecimal result = cashbackCalculationService.calculateCashbackAmount(operation, tariff);
        verify(operationCashbackRepository).findById(operationCashbackId);
        assertEquals(new BigDecimal("5.00").setScale(2), result.setScale(2));
    }
}