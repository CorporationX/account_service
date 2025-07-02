package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.TestContainersConfig;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestType;
import faang.school.accountservice.repository.request.RequestRepository;
import faang.school.accountservice.publisher.request.RequestKafkaPublisher;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RequestKafkaSchedulerIT extends TestContainersConfig {

    @Autowired
    private RequestRepository requestRepository;

    @SpyBean
    private RequestKafkaPublisher kafkaPublisher;

    @SpyBean
    private RequestKafkaScheduler scheduler;

    @Test
    void shouldSendKafkaNotificationForCompletedRequest() {
        UUID expectedId = UUID.fromString("985bc1d8-75aa-4e39-93ea-3f9a696e00bd");
        Request request = new Request();
        request.setIdempotencyToken(expectedId);
        request.setUserId(1L);
        request.setRequestType(RequestType.TRANSFER);
        request.setActive(false);
        request.setStatus(RequestStatus.COMPLETED);
        request.setLockedBy(1L);

        requestRepository.save(request);

        scheduler.processFinishedRequests();

        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(kafkaPublisher, timeout(2000).atLeastOnce()).sendMessage(captor.capture());

        Request sent = captor.getValue();
        assertThat(sent.getIdempotencyToken()).isEqualTo(expectedId);
    }
}
