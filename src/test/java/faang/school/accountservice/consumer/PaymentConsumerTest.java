package faang.school.accountservice.consumer;

import faang.school.accountservice.AccountServiceApplicationTests;
import faang.school.accountservice.dto.payment.kafka.PaymentAuthorizationRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentCancelRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentClearingRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
import jakarta.persistence.OptimisticLockException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static faang.school.accountservice.enums.AccountStatus.ACTIVE;
import static faang.school.accountservice.enums.AccountType.INDIVIDUAL_CURRENCY;
import static faang.school.accountservice.enums.Currency.RUB;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@DirtiesContext
@SpringBootTest
public class PaymentConsumerTest extends AccountServiceApplicationTests {

    @Value("${spring.kafka.topic.payments.authorization.request}")
    private String topicsAuthorizationRequest;
    @Value("${spring.kafka.topic.payments.authorization.response}")
    private String topicsAuthorizationResponse;
    @Value("${spring.kafka.topic.payments.clearing.request}")
    private String topicsClearingRequest;
    @Value("${spring.kafka.topic.payments.clearing.response}")
    private String topicsClearingResponse;
    @Value("${spring.kafka.topic.payments.cancel.request}")
    private String topicsCancelRequest;
    @Value("${spring.kafka.topic.payments.cancel.response}")
    private String topicsCancelResponse;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceService balanceService;

    private UUID uuidTransferId;
    private UUID uuidTransferId2;
    private Account save;
    private Account saveTwo;

    @BeforeEach
    void setUp() {
        balanceRepository.deleteAll();
        accountRepository.deleteAll();

        uuidTransferId = UUID.fromString("3d0393c2-5bc3-4079-9263-95e3be8c7886");
        uuidTransferId2 = UUID.fromString("3d0393c3-5bc3-4079-9263-95e3be8c7886");



        Account account = Account.builder()
                .accountNumber("14122001374784868680")
                .userId(1L)
                .type(INDIVIDUAL_CURRENCY)
                .currency(RUB)
                .status(ACTIVE)
                .description("testing")
                .version(0L)
                .build();
        save = accountRepository.save(account);

        Account accountTwo = Account.builder()
                .accountNumber("14122001374784868111")
                .userId(1L)
                .type(INDIVIDUAL_CURRENCY)
                .currency(RUB)
                .status(ACTIVE)
                .description("testing2")
                .version(0L)
                .build();
        saveTwo = accountRepository.save(accountTwo);


        Balance balance = Balance.builder()
                .account(save)
                .authorizedBalance(new BigDecimal(0))
                .actualBalance(new BigDecimal(100))
                .version(0L)
                .build();
        balanceRepository.save(balance);

        Balance balanceTwo = Balance.builder()
                .account(saveTwo)
                .authorizedBalance(new BigDecimal(0))
                .actualBalance(new BigDecimal(0))
                .version(0L)
                .build();
        balanceRepository.save(balanceTwo);
    }

    @Test
    public void optimisticLock_simultaneousAuthorization() throws InterruptedException {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        PaymentAuthorizationRequestDto paymentAuthorizationRequestDto = new PaymentAuthorizationRequestDto(
                save.getId(), new BigDecimal(70), uuidTransferId);

        PaymentAuthorizationRequestDto paymentAuthorizationRequestDto2 = new PaymentAuthorizationRequestDto(
                save.getId(), new BigDecimal(70), uuidTransferId2);

        sendKafkaMessage(topicsAuthorizationRequest, paymentAuthorizationRequestDto);
        sendKafkaMessage(topicsAuthorizationRequest, paymentAuthorizationRequestDto2);

        await().atMost(5, SECONDS)
                .pollInterval(100, MILLISECONDS)
                .untilAsserted(() -> {

                    Optional<Balance> resultOpt = balanceRepository.findByAccountId(save.getId());
                    assertThat(resultOpt).isPresent();

                    Balance result = resultOpt.get();
                    assertThat(result.getActualBalance()).isEqualByComparingTo(new BigDecimal("30"));
                    assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("70"));

                });

        Balance result;
        Optional<Balance> resultOpt = balanceRepository.findByAccountId(save.getId());
        if (resultOpt.isPresent()) {
            result = resultOpt.get();
            assertThat(result.getActualBalance()).isEqualByComparingTo(new BigDecimal("30"));
            assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("70"));
        } else {
            throw new RuntimeException("Optional is null");
        }
    }

    @Test
    public void pessimisticLock_simultaneouslyClearingAndCancel() throws InterruptedException {
        PaymentAuthorizationRequestDto paymentAuthorizationRequestDto = new PaymentAuthorizationRequestDto(
                save.getId(), new BigDecimal(70), uuidTransferId);
        sendKafkaMessage(topicsAuthorizationRequest, paymentAuthorizationRequestDto);

        await().atMost(3, SECONDS)
                .pollInterval(100, MILLISECONDS)
                .until(() -> {
                    Optional<Balance> balanceOpt = balanceRepository.findByAccountId(save.getId());
                    return balanceOpt.isPresent() &&
                            balanceOpt.get().getAuthorizedBalance().compareTo(new BigDecimal("70")) == 0;
                });

        PaymentClearingRequestDto paymentClearingRequestDto = new PaymentClearingRequestDto(save.getId(), saveTwo.getId(),
                new BigDecimal(70), uuidTransferId);
        PaymentCancelRequestDto paymentCancelRequestDto = new PaymentCancelRequestDto(save.getId(),
                new BigDecimal(70), uuidTransferId);

        sendKafkaMessage(topicsClearingRequest, paymentClearingRequestDto);
        sendKafkaMessage(topicsCancelRequest, paymentCancelRequestDto);

        await().atMost(5, SECONDS)
                .pollInterval(100, MILLISECONDS)
                .untilAsserted(() -> {
                    Balance result = balanceRepository.findByAccountId(save.getId())
                            .orElseThrow(() -> new RuntimeException("Balance not found for save account"));
                    Balance resultTwo = balanceRepository.findByAccountId(saveTwo.getId())
                            .orElseThrow(() -> new RuntimeException("Balance not found for saveTwo account"));

                    assertThat(result.getActualBalance()).isEqualByComparingTo(new BigDecimal("30"));
                    assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("0"));
                    assertThat(resultTwo.getActualBalance()).isEqualByComparingTo(new BigDecimal("70"));
                });
    }

    private void sendKafkaMessage(String topic, Object objectToSend) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, objectToSend);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(producerRecord);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.printf("Successfully sent event %s to topic '%s', partition: %s, offset: %s",
                        objectToSend,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                System.out.printf("Failed to send event %s to topic '%s': %s",
                        objectToSend,
                        topic,
                        ex.getMessage());
            }
        });
    }
}
