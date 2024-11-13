package faang.school.accountservice.service.cashback;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Merchant;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.cacheback.CashbackMerchant;
import faang.school.accountservice.entity.cacheback.CashbackOperationType;
import faang.school.accountservice.entity.cacheback.CashbackTariff;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.CashbackMerchantRepository;
import faang.school.accountservice.repository.CashbackOperationTypeRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.repository.MerchantRepository;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CashbackTariffService {
    private final AuthPaymentRepository authPaymentRepository;
    private final BalanceService balanceService;
    private final CashbackTariffRepository cashbackTariffRepository;
    private final CashbackOperationTypeRepository cashbackOperationTypeRepository;
    private final CashbackMerchantRepository cashbackMerchantRepository;
    private final MerchantRepository merchantRepository;

    @Transactional
    public CashbackTariff createTariff(CashbackTariff cashbackTariff) {
        cashbackTariff.setCreatedAt(LocalDateTime.now());
        cashbackTariffRepository.save(cashbackTariff);

        return cashbackTariff;
    }

    @Transactional
    public CashbackTariff updateTariff(UUID cashbackTariffId, CashbackTariff cashbackTariff) {
        CashbackTariff cashbackTariffDB = findCashbackTariffById(cashbackTariffId);

        cashbackTariffDB.setName(cashbackTariff.getName());
        cashbackTariffDB.setUpdatedAt(LocalDateTime.now());
        cashbackTariffRepository.save(cashbackTariffDB);

        return cashbackTariffDB;
    }

    @Transactional
    public CashbackOperationType attachOperationType(UUID tariffId, CashbackOperationType cashbackOperationType) {
        CashbackTariff cashbackTariff = findCashbackTariffById(tariffId);

        cashbackOperationType.setCashbackTariff(cashbackTariff);
        cashbackOperationType.setCreatedAt(LocalDateTime.now());

        cashbackOperationTypeRepository.save(cashbackOperationType);

        return cashbackOperationType;
    }

    @Transactional
    public CashbackOperationType updateOperationType(UUID id, CashbackOperationType operationType) {
        CashbackOperationType cashbackOperationType = findCashbackOperationTypeById(id);

        cashbackOperationType.setOperationType(operationType.getOperationType());
        cashbackOperationType.setCashbackPercentage(operationType.getCashbackPercentage());
        cashbackOperationType.setUpdatedAt(LocalDateTime.now());

        cashbackOperationTypeRepository.save(cashbackOperationType);

        return cashbackOperationType;
    }

    @Transactional
    public CashbackMerchant attachMerchantCashback(UUID tariffId, CashbackMerchant cashbackMerchant) {
        CashbackTariff cashbackTariff = findCashbackTariffById(tariffId);
        Merchant merchant = merchantRepository.findById(cashbackMerchant.getMerchant().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Merchant.class, cashbackMerchant.getMerchant().getId()));

        cashbackMerchant.setCashbackTariff(cashbackTariff);
        cashbackMerchant.setMerchant(merchant);
        cashbackMerchant.setCreatedAt(LocalDateTime.now());

        cashbackMerchantRepository.save(cashbackMerchant);

        return cashbackMerchant;
    }

    @Transactional
    public CashbackMerchant updateCashbackMerchant(UUID id, CashbackMerchant cashbackMerchant) {
        CashbackMerchant cashbackMerchantDB = findCashbackMerchantById(id);

        cashbackMerchantDB.setCashbackPercentage(cashbackMerchant.getCashbackPercentage());
        cashbackMerchantDB.setUpdatedAt(LocalDateTime.now());

        cashbackMerchantRepository.save(cashbackMerchantDB);

        return cashbackMerchantDB;
    }

    @Transactional(readOnly = true)
    public CashbackTariff getTariff(UUID id) {
        return findCashbackTariffById(id);
    }

    @Transactional(readOnly = true)
    public List<CashbackTariff> getTariffs() {
        return cashbackTariffRepository.findAll();
    }

    @Transactional
    public void removeOperationType(UUID id) {
        cashbackOperationTypeRepository.deleteById(id);
    }

    @Transactional
    public void removeMerchantCashback(UUID id) {
        cashbackMerchantRepository.deleteById(id);
    }

    @Transactional
    public void removeCashbackTariff(UUID id) {
        CashbackTariff cashbackTariff = findCashbackTariffById(id);

        cashbackTariff.getCashbackOperationTypes()
                .forEach(cashbackOperationType -> removeOperationType(cashbackOperationType.getId()));

        cashbackTariff.getCashbackMerchants()
                .forEach(cashbackMerchant -> removeMerchantCashback(cashbackMerchant.getId()));

        cashbackTariffRepository.deleteById(id);
    }

    public void calculateCashback(Account account, LocalDateTime startOfLastMonth, LocalDateTime endOfLastMonth) {
        Balance balance = account.getBalance();
        CashbackTariff cashbackTariff;
        UUID tariffId = account.getCashbackTariff().getId();

        cashbackTariff = cashbackTariffRepository.findByIdWithRelations(tariffId).orElseThrow(() ->
                new ResourceNotFoundException(CashbackTariff.class, tariffId));

        List<AuthPayment> payments = authPaymentRepository.findBySourceBalanceStatusAndPeriod(
                AuthPaymentStatus.CLOSED, balance.getId(), startOfLastMonth, endOfLastMonth
        );

        BigDecimal totalCashback = payments.stream()
                .map(payment -> calculateOperationCashback(payment, cashbackTariff))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("[%s] результат расчёта кешбека для accountId=%s [%s]".formatted(
                Thread.currentThread().getName(),
                account.getId(),
                totalCashback
        ));

        if (!totalCashback.equals(BigDecimal.ZERO)) {
            balanceService.saveCashback(balance, totalCashback);
        }
    }

    private BigDecimal calculateOperationCashback(AuthPayment payment, CashbackTariff cashbackTariff) {
        Set<CashbackMerchant> merchants = cashbackTariff.getCashbackMerchants();
        Set<CashbackOperationType> cashbackOperationTypes = cashbackTariff.getCashbackOperationTypes();

        BigDecimal cashbackPercentage = getCashbackPercentage(payment, merchants, cashbackOperationTypes);

        return payment.getAmount().multiply(cashbackPercentage.divide(BigDecimal.valueOf(100)));
    }

    private BigDecimal getCashbackPercentage(AuthPayment payment, Set<CashbackMerchant> merchants,
                                             Set<CashbackOperationType> cashbackOperationTypes) {
        Merchant paymentMerchant = getMerchant(payment.getTargetBalance().getAccount());

        Stream<BigDecimal> merchantPercentageStream = merchants.stream()
                .filter((merchant) -> paymentMerchant != null && merchant.getMerchant().equals(paymentMerchant))
                .map(CashbackMerchant::getCashbackPercentage);

        Stream<BigDecimal> cashbackOperationTypesStream = cashbackOperationTypes.stream()
                .filter((operationType) -> operationType.getOperationType().equals(payment.getCategory()))
                .map(CashbackOperationType::getCashbackPercentage);

        return Stream.concat(merchantPercentageStream, cashbackOperationTypesStream)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private Merchant getMerchant(Account account) {
        if (account.getUserId() != null) {
            return merchantRepository.findByUserId(account.getUserId());
        }

        return merchantRepository.findByProjectId(account.getProjectId());
    }

    private CashbackTariff findCashbackTariffById(UUID id) {
        return cashbackTariffRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException(CashbackTariff.class, id));
    }

    private CashbackOperationType findCashbackOperationTypeById(UUID id) {
        return cashbackOperationTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CashbackOperationType.class, id));
    }

    private CashbackMerchant findCashbackMerchantById(UUID id) {
        return cashbackMerchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CashbackMerchant.class, id));
    }
}
