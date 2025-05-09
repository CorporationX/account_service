package faang.school.accountservice.handler;

import faang.school.accountservice.dto.event.PaymentOperationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOperationEventHandler {

    public void handle(PaymentOperationEvent event) {

    }
}
