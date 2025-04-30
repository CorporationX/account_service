package faang.school.accountservice.balance;


import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.TransactionsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Testcontainers
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class BalanceRepositoryTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testingRepos() {
        Account accountSender = new Account();
        accountSender.setNumber("123456789012");
        accountSender.setOwnerType(OwnerType.USER);
        accountSender.setOwnerId(1L);
        accountSender.setType(AccountType.DEPOSIT);
        accountSender.setCurrency(Currency.USD);
        accountSender.setStatus(AccountStatus.ACTIVE);
        accountSender.setCreatedAt(LocalDateTime.now());
        accountSender.setUpdatedAt(LocalDateTime.now());
        accountSender.setVersion(0);

        Account accountReceiver = new Account();
        accountReceiver.setNumber("223456789012");
        accountReceiver.setOwnerType(OwnerType.USER);
        accountReceiver.setOwnerId(2L);
        accountReceiver.setType(AccountType.DEPOSIT);
        accountReceiver.setCurrency(Currency.USD);
        accountReceiver.setStatus(AccountStatus.ACTIVE);
        accountReceiver.setCreatedAt(LocalDateTime.now());
        accountReceiver.setUpdatedAt(LocalDateTime.now());
        accountReceiver.setVersion(0);

        accountRepository.saveAll(List.of(accountSender, accountReceiver));

        Balance sender = Balance.builder()
                .account(accountSender)
                .availableBalance(new BigDecimal("100"))
                .currentBalance(new BigDecimal("100"))
                .build();

        Balance receiver = Balance.builder()
                .account(accountReceiver)
                .availableBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .build();

        balanceRepository.saveAll(List.of(sender, receiver));

        Transaction sendTx = Transaction.builder()
                .balance(sender)
                .amount(new BigDecimal("10"))
                .type(TransactionType.SEND)
                .comment("Test transfer")
                .build();

        Transaction receiveTx = Transaction.builder()
                .balance(receiver)
                .amount(new BigDecimal("10"))
                .type(TransactionType.RECEIVE)
                .comment("Test receive")
                .build();

        transactionsRepository.saveAll(List.of(sendTx, receiveTx));


        assertThat(balanceRepository.findAll())
                .hasSize(2)
                .extracting(Balance::getAvailableBalance)
                .containsExactlyInAnyOrder(
                        new BigDecimal("100"),
                        BigDecimal.ZERO);

        assertThat(transactionsRepository.findAll())
                .hasSize(2)
                .allSatisfy(transaction -> {
                    assertThat(transaction.getAmount()).isEqualByComparingTo("10");
                    assertThat(transaction.getComment()).isNotNull();
                })
                .extracting(Transaction::getType)
                .containsExactlyInAnyOrder(
                        TransactionType.SEND,
                        TransactionType.RECEIVE);

        assertThat(accountRepository.findAll())
                .hasSize(2)
                .extracting(Account::getNumber)
                .containsExactlyInAnyOrder(
                        "123456789012",
                        "223456789012");

        Balance savedSender = balanceRepository.findById(sender.getId()).orElseThrow();
        assertThat(savedSender.getAccount())
                .isNotNull()
                .extracting(Account::getNumber)
                .isEqualTo("123456789012");

        assertThat(accountSender.getVersion()).isZero();
        assertThat(accountReceiver.getVersion()).isZero();
    }

}