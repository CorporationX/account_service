package faang.school.accountservice.listener.transfer;

import faang.school.accountservice.listener.AbstractEventListener;
import faang.school.accountservice.publisher.BrokenTransferEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractTransferEventListener<T> extends AbstractEventListener<T> {

    private final String topic;
    private final BrokenTransferEventPublisher brokenTransferEventPublisher;

    @Override
    public void handleError(T event) {
        brokenTransferEventPublisher.publish(topic, event);
    }
}