package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Merchant;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.cacheback.CashbackMerchant;
import faang.school.accountservice.entity.cacheback.CashbackOperationType;
import faang.school.accountservice.entity.cacheback.CashbackTariff;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.enums.pending.Category;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.CashbackMerchantRepository;
import faang.school.accountservice.repository.CashbackOperationTypeRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.repository.MerchantRepository;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.cashback.CashbackTariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CashbackTariffServiceTest {
    @Mock
    private AuthPaymentRepository authPaymentRepository;
    @Mock
    private BalanceService balanceService;
    @Mock
    private CashbackTariffRepository cashbackTariffRepository;
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private CashbackOperationTypeRepository cashbackOperationTypeRepository;
    @Mock
    private CashbackMerchantRepository cashbackMerchantRepository;
    @InjectMocks
    private CashbackTariffService cashbackTariffService;

    private CashbackTariff cashbackTariff;
    private Merchant merchant;
    private CashbackMerchant cashbackMerchant;
    private CashbackOperationType cashbackOperationType;
    private Account account;
    private LocalDateTime startOfLastMonth = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    private LocalDateTime endOfLastMonth = LocalDateTime.of(2024, 1, 31, 23, 59, 59);

    @BeforeEach
    public void setUp() {
        merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .userId(1L)
                .build();

        cashbackMerchant = CashbackMerchant.builder()
                .cashbackTariff(CashbackTariff.builder().id(UUID.randomUUID()).build())
                .cashbackPercentage(BigDecimal.valueOf(15))
                .merchant(merchant)
                .build();

        cashbackOperationType = CashbackOperationType.builder()
                .cashbackTariff(CashbackTariff.builder().id(UUID.randomUUID()).build())
                .cashbackPercentage(BigDecimal.valueOf(10.05))
                .operationType(Category.OTHER)
                .build();

        cashbackTariff = CashbackTariff.builder()
                .id(UUID.randomUUID())
                .name("Test Tariff")
                .cashbackMerchants(Set.of(cashbackMerchant))
                .cashbackOperationTypes(Set.of(cashbackOperationType))
                .build();

        account = Account.builder()
                .id(UUID.randomUUID())
                .balance(Balance.builder().id(UUID.randomUUID()).build())
                .cashbackTariff(cashbackTariff)
                .build();
    }

    @Test
    public void testCreateTariff() {
        CashbackTariff result = cashbackTariffService.createTariff(cashbackTariff);

        assertNotNull(result.getCreatedAt());
        verify(cashbackTariffRepository).save(cashbackTariff);
    }

    @Test
    public void testFindTariffs() {
        when(cashbackTariffRepository.findAll())
                .thenReturn(List.of(cashbackTariff));

        List<CashbackTariff> result = cashbackTariffService.getTariffs();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), cashbackTariff);
        verify(cashbackTariffRepository).findAll();
    }

    @Test
    public void testFindOneTariff() {
        UUID tariffId = cashbackTariff.getId();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        CashbackTariff result = cashbackTariffService.getTariff(tariffId);

        assertEquals(result, cashbackTariff);
        verify(cashbackTariffRepository).findByIdWithRelations(tariffId);
    }

    @Test
    public void testFindOneTariffNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService.getTariff(cashbackTariff.getId()));
        verify(cashbackTariffRepository).findByIdWithRelations(cashbackTariff.getId());
    }

    @Test
    public void testUpdateTariff() {
        UUID tariffId = cashbackTariff.getId();

        CashbackTariff updateTariff = CashbackTariff.builder()
                .name("Change Name")
                .build();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        CashbackTariff result = cashbackTariffService.updateTariff(tariffId, updateTariff);

        assertEquals(result.getName(), updateTariff.getName());
        assertNotNull(result.getUpdatedAt());
        verify(cashbackTariffRepository).save(cashbackTariff);
    }

    @Test
    public void testUpdateTariffNotFound() {
        UUID tariffId = cashbackTariff.getId();

        CashbackTariff updateTariff = CashbackTariff.builder()
                .name("Change Name")
                .build();

        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService.updateTariff(tariffId, updateTariff));

        verify(cashbackTariffRepository, times(0)).save(cashbackTariff);
    }

    @Test
    public void testAttachMerchantCashback() {
        UUID tariffId = cashbackTariff.getId();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        when(merchantRepository.findById(merchant.getId()))
                .thenReturn(Optional.ofNullable(merchant));

        CashbackMerchant result = cashbackTariffService.attachMerchantCashback(tariffId, cashbackMerchant);

        assertEquals(result.getMerchant(), merchant);
        assertEquals(result.getCashbackTariff(), cashbackTariff);
        assertNotNull(result.getCreatedAt());

        verify(cashbackMerchantRepository).save(cashbackMerchant);
    }

    @Test
    public void testUpdateMerchantCashback() {
        when(cashbackMerchantRepository.findById(cashbackMerchant.getId()))
                .thenReturn(Optional.ofNullable(cashbackMerchant));

        CashbackMerchant updateMerchant = CashbackMerchant.builder()
                .cashbackPercentage(new BigDecimal("99.9"))
                .build();

        CashbackMerchant result = cashbackTariffService.updateCashbackMerchant(
                cashbackMerchant.getId(), updateMerchant);

        assertEquals(updateMerchant.getCashbackPercentage(), result.getCashbackPercentage());
        assertNotNull(result.getUpdatedAt());

        verify(cashbackMerchantRepository).save(cashbackMerchant);
    }

    @Test
    public void testUpdateMerchantCashbackNotFound() {
        CashbackMerchant updateMerchant = CashbackMerchant.builder()
                .cashbackPercentage(new BigDecimal("99.9"))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService
                .updateCashbackMerchant(cashbackMerchant.getId(), updateMerchant));

        verify(cashbackMerchantRepository, times(0)).save(cashbackMerchant);
    }

    @Test
    public void testAttachMerchantCashbackMerchantNotFound() {
        UUID tariffId = cashbackTariff.getId();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService
                .attachMerchantCashback(tariffId, cashbackMerchant));

        verify(cashbackMerchantRepository, times(0)).save(cashbackMerchant);
    }

    @Test
    public void testAttachMerchantCashbackTariffNotFound() {
        UUID tariffId = cashbackTariff.getId();

        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService
                .attachMerchantCashback(tariffId, cashbackMerchant));

        verify(cashbackMerchantRepository, times(0)).save(cashbackMerchant);
    }

    @Test
    public void testAttachOperationType() {
        UUID tariffId = cashbackTariff.getId();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId)).thenReturn(Optional.ofNullable(cashbackTariff));

        CashbackOperationType result = cashbackTariffService.attachOperationType(tariffId, cashbackOperationType);

        assertEquals(result.getCashbackTariff(), cashbackTariff);
        assertNotNull(result.getCreatedAt());

        verify(cashbackOperationTypeRepository).save(cashbackOperationType);
    }

    @Test
    public void testUpdateOperationType() {
        when(cashbackOperationTypeRepository.findById(cashbackOperationType.getId()))
                .thenReturn(Optional.ofNullable(cashbackOperationType));

        CashbackOperationType updateOperationType = CashbackOperationType.builder()
                .operationType(Category.OTHER)
                .cashbackPercentage(new BigDecimal("99.9"))
                .build();

        CashbackOperationType result = cashbackTariffService.updateOperationType(
                cashbackOperationType.getId(), updateOperationType);

        assertEquals(result.getOperationType(), updateOperationType.getOperationType());
        assertEquals(result.getCashbackPercentage(), updateOperationType.getCashbackPercentage());
        assertNotNull(result.getUpdatedAt());

        verify(cashbackOperationTypeRepository).save(cashbackOperationType);
    }

    @Test
    public void testUpdateOperationTypeNotFound() {
        CashbackOperationType updateOperationType = CashbackOperationType.builder()
                .operationType(Category.OTHER)
                .cashbackPercentage(new BigDecimal("99.9"))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService
                .updateOperationType(cashbackOperationType.getId(), updateOperationType));

        verify(cashbackOperationTypeRepository, times(0)).save(cashbackOperationType);
    }

    @Test
    public void testAttachOperationTypeCashbackTariffNotFound() {
        UUID tariffId = cashbackTariff.getId();

        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService
                .attachOperationType(tariffId, cashbackOperationType));

        verify(cashbackOperationTypeRepository, times(0)).save(cashbackOperationType);
    }

    @Test
    public void testCalculateCashback() {
        UUID tariffId = account.getCashbackTariff().getId();
        UUID balanceId = account.getBalance().getId();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        when(authPaymentRepository.findBySourceBalanceStatusAndPeriod(
                AuthPaymentStatus.CLOSED, balanceId, startOfLastMonth, endOfLastMonth))
                .thenReturn(makeAuthPayment());

        when(merchantRepository.findByUserId(1L)).thenReturn(merchant);

        cashbackTariffService.calculateCashback(account, startOfLastMonth, endOfLastMonth);

        verify(balanceService).saveCashback(account.getBalance(), new BigDecimal("165.00"));
    }

    @Test
    public void testCalculateCashbackTariffNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> cashbackTariffService
                .calculateCashback(account, startOfLastMonth, endOfLastMonth));

        verify(balanceService, times(0)).saveCashback(any(Balance.class), any(BigDecimal.class));
    }

    @Test
    public void testCalculateCashbackEmptyPayments() {
        UUID tariffId = account.getCashbackTariff().getId();
        UUID balanceId = account.getBalance().getId();

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        when(authPaymentRepository.findBySourceBalanceStatusAndPeriod(
                AuthPaymentStatus.CLOSED, balanceId, startOfLastMonth, endOfLastMonth))
                .thenReturn(new ArrayList<>());

        cashbackTariffService.calculateCashback(account, startOfLastMonth, endOfLastMonth);

        verify(balanceService, times(0)).saveCashback(any(Balance.class), any(BigDecimal.class));
    }

    @Test
    public void testCalculateCashbackEmptyMerchants() {
        UUID tariffId = account.getCashbackTariff().getId();
        UUID balanceId = account.getBalance().getId();
        cashbackTariff.setCashbackMerchants(new HashSet<>());

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        when(authPaymentRepository.findBySourceBalanceStatusAndPeriod(
                AuthPaymentStatus.CLOSED, balanceId, startOfLastMonth, endOfLastMonth))
                .thenReturn(makeAuthPayment());

        when(merchantRepository.findByUserId(1L)).thenReturn(merchant);

        cashbackTariffService.calculateCashback(account, startOfLastMonth, endOfLastMonth);

        verify(balanceService).saveCashback(account.getBalance(), new BigDecimal("100.5000"));
    }

    @Test
    public void testCalculateCashbackEmptyOperation() {
        UUID tariffId = account.getCashbackTariff().getId();
        UUID balanceId = account.getBalance().getId();
        cashbackTariff.setCashbackOperationTypes(new HashSet<>());

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        when(authPaymentRepository.findBySourceBalanceStatusAndPeriod(
                AuthPaymentStatus.CLOSED, balanceId, startOfLastMonth, endOfLastMonth))
                .thenReturn(makeAuthPayment());

        when(merchantRepository.findByUserId(1L)).thenReturn(merchant);

        cashbackTariffService.calculateCashback(account, startOfLastMonth, endOfLastMonth);

        verify(balanceService).saveCashback(account.getBalance(), new BigDecimal("165.00"));
    }

    @Test
    public void testCalculateCashbackEmptyTariff() {
        UUID tariffId = account.getCashbackTariff().getId();
        UUID balanceId = account.getBalance().getId();
        cashbackTariff.setCashbackOperationTypes(new HashSet<>());
        cashbackTariff.setCashbackMerchants(new HashSet<>());

        when(cashbackTariffRepository.findByIdWithRelations(tariffId))
                .thenReturn(Optional.ofNullable(cashbackTariff));

        when(authPaymentRepository.findBySourceBalanceStatusAndPeriod(
                AuthPaymentStatus.CLOSED, balanceId, startOfLastMonth, endOfLastMonth))
                .thenReturn(makeAuthPayment());

        when(merchantRepository.findByUserId(1L)).thenReturn(merchant);

        cashbackTariffService.calculateCashback(account, startOfLastMonth, endOfLastMonth);

        verify(balanceService, times(0)).saveCashback(any(Balance.class), any(BigDecimal.class));
    }

    private List<AuthPayment> makeAuthPayment() {
        return List.of(
                AuthPayment.builder()
                        .amount(BigDecimal.valueOf(1000L))
                        .targetBalance(Balance.builder().account(Account.builder().userId(1L).build()).build())
                        .category(Category.OTHER)
                        .build(),
                AuthPayment.builder()
                        .amount(BigDecimal.valueOf(100L))
                        .targetBalance(Balance.builder().account(Account.builder().userId(1L).build()).build())
                        .category(Category.CHARITY)
                        .build()
        );
    }
}
