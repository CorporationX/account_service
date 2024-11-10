package faang.school.accountservice.publisher.payment.kafka.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.response.AuthPaymentResponse;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.publisher.payment.kafka.AbstractToKafkaPaymentPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.enums.payment.AccountBalanceStatus.BALANCE_NOT_VERIFIED;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.INSUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.payment.PaymentStatus.FAILED;
import static faang.school.accountservice.enums.payment.PaymentStatus.SUCCESS;

@Slf4j
@Getter
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
@Component
public class AuthPaymentToKafkaPublisher extends AbstractToKafkaPaymentPublisher<AuthPaymentResponse> {
    @Value("${spring.kafka.topic.auth-payment.response}")
    private String topicName;

    public AuthPaymentToKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                       ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper, AuthPaymentResponse.class);
    }

    @Override
    public void makeResponse(Object... args) {
        AuthPayment authPayment = (AuthPayment) args[0];
        setResponse(new AuthPaymentResponse(authPayment.getId(), SUFFICIENT_FUNDS, SUCCESS));
    }

    @Override
    public void makeErrorResponse(Object... args) {
        AuthPaymentRequest authPaymentRequest = (AuthPaymentRequest) args[0];
        Exception exception = (Exception) args[1];

        if (exception instanceof ValidationException) {
            setResponse(new AuthPaymentResponse(authPaymentRequest.getOperationId(), INSUFFICIENT_FUNDS, FAILED));
        } else {
            setResponse(new AuthPaymentResponse(authPaymentRequest.getOperationId(), BALANCE_NOT_VERIFIED, FAILED));
        }
    }
}
