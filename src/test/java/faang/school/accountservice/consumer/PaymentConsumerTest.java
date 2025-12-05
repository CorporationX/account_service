package faang.school.accountservice.consumer;

import faang.school.accountservice.AccountServiceApplicationTests;
import faang.school.accountservice.dto.payment.kafka.PaymentAuthorizationRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static faang.school.accountservice.enums.AccountStatus.ACTIVE;
import static faang.school.accountservice.enums.AccountType.INDIVIDUAL_CURRENCY;
import static faang.school.accountservice.enums.Currency.RUB;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
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

    private UUID uuidTransferId;
    private UUID uuidTransferId2;
    private Account save;

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

        Balance balance = Balance.builder()
                .account(save)
                .authorizedBalance(new BigDecimal(0))
                .actualBalance(new BigDecimal(100))
                .version(0L)
                .build();

        balanceRepository.save(balance);

    }

    @Test
    public void optimisticLock_simultaneousAuthorization() throws InterruptedException {
        PaymentAuthorizationRequestDto paymentAuthorizationRequestDto = new PaymentAuthorizationRequestDto(
                save.getId(), new BigDecimal(70), uuidTransferId);

        PaymentAuthorizationRequestDto paymentAuthorizationRequestDto2 = new PaymentAuthorizationRequestDto(
                save.getId(), new BigDecimal(70), uuidTransferId2);

        sendKafkaMessage(topicsAuthorizationRequest, paymentAuthorizationRequestDto);
        sendKafkaMessage(topicsAuthorizationRequest, paymentAuthorizationRequestDto2);

        Thread.sleep(3000);

        Balance result;
        Optional<Balance> resultOpt = balanceRepository.findByAccountId(save.getId());
        if (resultOpt.isPresent()) {
            result = resultOpt.get();
            assertThat(result.getActualBalance()).isEqualByComparingTo(new BigDecimal("30"));
            assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("70"));
        } else {
            throw new RuntimeException();
        }
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
