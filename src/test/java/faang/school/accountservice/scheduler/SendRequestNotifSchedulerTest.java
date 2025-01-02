package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.redis.RedisTopicProperties;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.message.event.RequestInProgressEvent;
import faang.school.accountservice.message.producer.MessagePublisher;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SendRequestNotifSchedulerTest {

    @Mock
    private RequestService requestService;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private RedisTopicProperties redisTopicProperties;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private SendRequestNotifScheduler scheduler;

    private Request request1;
    private Request request2;
    private List<Request> requests;
    private RequestInProgressEvent event1;
    private RequestInProgressEvent event2;
    private String requestInProgressTopic;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduler, "limit", 100);

        request1 = new Request();
        request2 = new Request();
        requests = Arrays.asList(request1, request2);

        event1 = new RequestInProgressEvent();
        event2 = new RequestInProgressEvent();

        requestInProgressTopic = "request-in-progress";
    }

    @Test
    void sendNotifications_WhenRequestsExist_ShouldPublishEvents() {

        when(requestService.getRequestsToExecute(RequestType.REMITTANCE, 100))
                .thenReturn(requests);
        when(requestMapper.toRequestInProgressEvent(request1)).thenReturn(event1);
        when(requestMapper.toRequestInProgressEvent(request2)).thenReturn(event2);
        when(redisTopicProperties.getRequestInProgressTopic()).thenReturn(requestInProgressTopic);

        scheduler.sendNotifications();

        verify(messagePublisher, times(2)).publish(requestInProgressTopic, event1);
        verify(messagePublisher, times(2)).publish(requestInProgressTopic, event2);
        verify(requestService).getRequestsToExecute(RequestType.REMITTANCE, 100);
    }

    @Test
    void sendNotifications_WhenNoRequests_ShouldNotPublishEvents() {
        when(requestService.getRequestsToExecute(RequestType.REMITTANCE, 100))
                .thenReturn(Collections.emptyList());

        scheduler.sendNotifications();

        verify(messagePublisher, never()).publish(anyString(), any());
    }
}
