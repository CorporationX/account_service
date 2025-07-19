package faang.school.accountservice.kafka.producer;

import faang.school.accountservice.event.RequestEvent;

public interface DataSender {
    void send(String topic, RequestEvent requestEvent);
}
