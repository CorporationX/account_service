package faang.school.accountservice.service;

import faang.school.accountservice.model.event.RequestEvent;

public interface PaymentRequestService {
    void authorize(RequestEvent requestEvent);
    void cancel(RequestEvent requestEvent);
}
