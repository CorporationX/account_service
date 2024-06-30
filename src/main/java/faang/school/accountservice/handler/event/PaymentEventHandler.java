package faang.school.accountservice.handler.event;

import faang.school.accountservice.dto.event.PaymentEvent;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventHandler implements EventHandler<PaymentEvent> {


    @Override
    public boolean canHandle(PaymentEvent event) {
        return true;
    }

    @Override
    public void handle(PaymentEvent event) {
        //some logic
    }
}
