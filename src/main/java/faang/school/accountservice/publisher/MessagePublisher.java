package faang.school.accountservice.publisher;

import faang.school.accountservice.event.Event;

public interface MessagePublisher<T extends Event> {
    void publish(T event);
}
