package faang.school.accountservice.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("it")
public class BalanceIntegrationTest {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestRestTemplate restTemplate;


    @LocalServerPort
    private int port;

    @AfterEach
    void cleanUp() {
        transactionsRepository.deleteAll();
        balanceRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void depositFunds() {
        Account account = createTestAccount();
        Balance balance = createTestBalance(account, new BigDecimal("100.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-user-id", "1");

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/balances/" + balance.getId() + "/deposit/50.00",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Balance updatedBalance = balanceRepository.findById(balance.getId()).orElseThrow();
        assertThat(updatedBalance.getCurrentBalance()).isEqualByComparingTo("150.00");

        List<Transaction> transactions = transactionsRepository.findAll();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
    }

    @Test
    void withdrawFunds() {
        Account account = createTestAccount();
        Balance balance = createTestBalance(account, new BigDecimal("100.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-user-id", "1");

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/balances/" + balance.getId() + "/withdraw/50.00",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Balance updatedBalance = balanceRepository.findById(balance.getId()).orElseThrow();
        assertThat(updatedBalance.getCurrentBalance()).isEqualByComparingTo("50.00");

        List<Transaction> transactions = transactionsRepository.findAll();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getType()).isEqualTo(TransactionType.WITHDRAW);
    }

    @Test
    void sendPayment() {
        Account receiverAccount = createTestAccount();
        receiverAccount.setNumber("223456789012");
        accountRepository.save(receiverAccount);
        Account senderAccount = createTestAccount();

        Balance senderBalance = createTestBalance(senderAccount, new BigDecimal("100"));
        Balance receiverBalance = createTestBalance(receiverAccount, new BigDecimal("100"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-user-id", "1");

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/balances/" + senderBalance.getId() +
                        "/send/" + receiverBalance.getId() + "/10.15",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Balance updatedBalanceSender = balanceRepository.findById(senderBalance.getId()).orElseThrow();
        assertThat(updatedBalanceSender.getCurrentBalance()).isEqualByComparingTo("89.85");

        Balance updateBalanceReceiver = balanceRepository.findById(receiverBalance.getId()).orElseThrow();
        assertThat(updateBalanceReceiver.getCurrentBalance()).isEqualByComparingTo("110.15");

        List<Transaction> transactions = transactionsRepository.findAll();
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getType()).isEqualTo(TransactionType.SEND);
        assertThat(transactions.get(1).getType()).isEqualTo(TransactionType.RECEIVE);
    }

    @Test
    void getBalance() {
        Account account = createTestAccount();
        Balance balance = createTestBalance(account, new BigDecimal("32.12"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-user-id", "1");

        ResponseEntity<BalanceResponseDto> response = restTemplate.exchange(
                "/balances/" + balance.getId(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BalanceResponseDto.class
        );

        assertThat(response.getBody())
                .extracting(BalanceResponseDto::currentBalance,
                        BalanceResponseDto::availableBalance)
                .containsExactly(new BigDecimal("32.12"),
                        new BigDecimal("32.12"));
    }

    private Account createTestAccount() {
        Account account = new Account();
        account.setNumber("123456789012");
        account.setOwnerType(OwnerType.USER);
        account.setOwnerId(1L);
        account.setType(AccountType.DEPOSIT);
        account.setCurrency(Currency.USD);
        account.setStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }


    private Balance createTestBalance(Account account, BigDecimal amount) {
        Balance balance = Balance.builder()
                .account(account)
                .currentBalance(amount)
                .availableBalance(amount)
                .build();
        return balanceRepository.save(balance);
    }

}
