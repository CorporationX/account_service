package faang.school.accountservice.handler;

import faang.school.accountservice.dto.event.RequestOutboxEvent;
import faang.school.accountservice.service.RequestPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestOutboxEventHandler {

    private final RequestPaymentService requestPaymentService;

    public void handle(RequestOutboxEvent event) {

    }
}
