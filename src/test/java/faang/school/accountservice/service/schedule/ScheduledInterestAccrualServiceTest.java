package faang.school.accountservice.service.schedule;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.enums.AccountStatus;
import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.model.account.enums.Currency;
import faang.school.accountservice.model.account.enums.OwnerType;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.model.tariff.TariffType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.util.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ScheduledInterestAccrualServiceTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ScheduledInterestAccrualServiceTest.class);

    @Autowired
    private TariffRepository tariffRepository;
    @Autowired
    private SavingsAccountRepository savingsAccountRepository;
    @Autowired
    private ScheduledInterestAccrualService scheduledInterestAccrualService;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void scheduleDailyInterestAccrualTest() {
        Account account = Account.builder()
                .number("12345678912124")
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .type(AccountType.CURRENT_INDIVIDUAL)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .build();

        account = accountRepository.save(account);

        Tariff tariff = Tariff.builder()
                .type(TariffType.BASIC)
                .rateHistory(List.of(new BigDecimal("20.00")))
                .build();

        tariff = tariffRepository.save(tariff);

        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .balance(new BigDecimal("1000.00"))
                .tariffHistoryIds(List.of(tariff.getId()))
                .createdAt(LocalDateTime.now().minusDays(10))
                .lastInterestDate(LocalDateTime.now().minusDays(1))
                .build();

        savingsAccount = savingsAccountRepository.save(savingsAccount);

        scheduledInterestAccrualService.scheduleDailyInterestAccrual();
        SavingsAccount updated = savingsAccountRepository.findById(savingsAccount.getId()).orElseThrow();
        assertThat(updated.getBalance()).isGreaterThan(new BigDecimal("1000.00"));
        assertThat(updated.getLastInterestDate()).isNotEqualTo(savingsAccount.getLastInterestDate());

        log.info("Updated balance = {}", updated.getBalance());
    }
}