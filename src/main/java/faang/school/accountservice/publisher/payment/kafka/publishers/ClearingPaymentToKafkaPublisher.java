package faang.school.accountservice.publisher.payment.kafka.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
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
public class ClearingPaymentToKafkaPublisher extends AbstractToKafkaPaymentPublisher<ClearingPaymentResponse> {
    @Value("${spring.kafka.topic.clearing-payment.response}")
    private String topicName;

    public ClearingPaymentToKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                           ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper, ClearingPaymentResponse.class);
    }

    @Override
    public void makeResponse(Object... args) {
        AuthPayment authPayment = (AuthPayment) args[0];
        setResponse(new ClearingPaymentResponse(authPayment.getId(), SUCCESS));
    }

    @Override
    public void makeErrorResponse(Object... args) {
        ClearingPaymentRequest clearingPaymentRequest = (ClearingPaymentRequest) args[0];
        setResponse(new ClearingPaymentResponse(clearingPaymentRequest.getOperationId(), FAILED));
    }
}
