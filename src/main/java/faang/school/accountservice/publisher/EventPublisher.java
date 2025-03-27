package faang.school.accountservice.publisher;

import faang.school.accountservice.model.Request;

public interface EventPublisher {

    void publish(Request request);
}
