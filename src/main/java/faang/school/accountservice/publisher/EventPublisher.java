package faang.school.accountservice.publisher;

import faang.school.accountservice.entity.Request;

public interface EventPublisher {

    void publish(Request request);
}
