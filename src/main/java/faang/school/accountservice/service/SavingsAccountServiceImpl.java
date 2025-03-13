package faang.school.accountservice.service;

import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private static final Logger logger = LoggerFactory.getLogger(SavingsAccountServiceImpl.class);
    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;

    @Override
    @Transactional
    public SavingsAccount createSavingsAccount(Long accountId, Long tariffId) {
        logger.info("Creating savings account for accountId: {} with tariffId: {}", accountId, tariffId);

        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new IllegalArgumentException("Tariff not found"));

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(accountId);
        savingsAccount.setBalance(BigDecimal.ZERO);
        savingsAccount.setTariffHistory(List.of(tariff.getId()));
        savingsAccount.setLastInterestCalculationDate(LocalDate.now());
        savingsAccount.setCreatedAt(LocalDate.now().atStartOfDay());
        savingsAccount.setUpdatedAt(LocalDate.now().atStartOfDay());

        SavingsAccount savedAccount = savingsAccountRepository.save(savingsAccount);
        logger.info("Savings account created successfully with ID: {}", savedAccount.getId());
        return savedAccount;
    }

    @Override
    public SavingsAccount getSavingsAccountById(Long id) {
        logger.info("Fetching savings account by ID: {}", id);
        return savingsAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Savings account not found"));
    }

    @Override
    public SavingsAccount getSavingsAccountByAccountId(Long accountId) {
        logger.info("Fetching savings account for accountId: {}", accountId);
        return savingsAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Savings account not found"));
    }
}
