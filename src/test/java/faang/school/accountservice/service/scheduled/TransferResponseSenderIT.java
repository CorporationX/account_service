package faang.school.accountservice.service.scheduled;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransferResponseSenderIT extends BaseIntegrationTest {
    private static final UUID TRANSFER_ID = UUID.fromString("5d14fdc1-8327-4cb0-b102-f59ff9cbf5d8");

    @Autowired
    private TransferRepository transferRepository;

    @Sql(scripts = {
            "/cleanup-test-data.sql",
            "/test-data-accounts-balances.sql",
            "/test-data-auth-payments.sql",
            "/test-data-transfer-requests.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testScheduledRetryUnsentPayments() {
        await().atMost(30, TimeUnit.SECONDS).until(
                () -> transferRepository.findById(TRANSFER_ID).map(TransferRequest::isKafkaPublished).orElse(false));

        TransferRequest updatedRequest = transferRepository.findById(TRANSFER_ID).orElseThrow();
        assertThat(updatedRequest.isKafkaPublished()).isTrue();
    }
}
