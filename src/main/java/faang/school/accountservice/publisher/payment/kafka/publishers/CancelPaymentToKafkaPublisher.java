package faang.school.accountservice.publisher.payment.kafka.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.publisher.payment.kafka.AbstractToKafkaPaymentPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.enums.payment.PaymentStatus.FAILED;
import static faang.school.accountservice.enums.payment.PaymentStatus.SUCCESS;

@Slf4j
@Getter
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
@Component
public class CancelPaymentToKafkaPublisher extends AbstractToKafkaPaymentPublisher<CancelPaymentResponse> {
    @Value("${spring.kafka.topic.cancel-payment.response}")
    private String topicName;

    public CancelPaymentToKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                         ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper, CancelPaymentResponse.class);
    }

    @Override
    public void makeResponse(Object... args) {
        AuthPayment authPayment = (AuthPayment) args[0];
        setResponse(new CancelPaymentResponse(authPayment.getId(), SUCCESS));
    }

    @Override
    public void makeErrorResponse(Object... args) {
        CancelPaymentRequest cancelPaymentRequest = (CancelPaymentRequest) args[0];
        setResponse(new CancelPaymentResponse(cancelPaymentRequest.getOperationId(), FAILED));
    }
}
