package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.PaymentRequest;
import faang.school.accountservice.dto.payment.response.PaymentResponse;
import faang.school.accountservice.exception.payment.InsufficientFundsException;
import faang.school.accountservice.model.payment.Payment;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static faang.school.accountservice.model.payment.AccountBalanceStatus.*;
import static faang.school.accountservice.model.payment.PaymentResponseStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthPaymentPublisherTest {

    private static final UUID OPERATION_ID = UUID.randomUUID();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthPaymentPublisher publisher;

    private Payment payment;
    private PaymentRequest paymentRequest;
    private Exception insufficientFundsException;
    private Exception optimisticLockException;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(OPERATION_ID)
                .build();

        paymentRequest = PaymentRequest.builder()
                .operationId(OPERATION_ID)
                .build();

        insufficientFundsException = new InsufficientFundsException("");

        optimisticLockException = new OptimisticLockException("");
    }

    @Test
    void testMakeResponse_successful() throws JsonProcessingException {
        publisher.makeResponse(payment);
        publisher.publish();

        ArgumentCaptor<PaymentResponse> responseCaptor = ArgumentCaptor.forClass(PaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        PaymentResponse expected = PaymentResponse.builder()
                .operationId(OPERATION_ID)
                .status(SUFFICIENT_FUNDS)
                .paymentResponseStatus(SUCCESS)
                .build();

        assertThat(responseCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void testMakeErrorResponse_successful_validationException() throws JsonProcessingException {
        publisher.makeErrorResponse(paymentRequest, insufficientFundsException);
        publisher.publish();

        ArgumentCaptor<PaymentResponse> responseCaptor = ArgumentCaptor.forClass(PaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        PaymentResponse expected = PaymentResponse.builder()
                .operationId(OPERATION_ID)
                .status(INSUFFICIENT_FUNDS)
                .paymentResponseStatus(FAILED)
                .build();

        assertThat(responseCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void testMakeErrorResponse_successful_otherException() throws JsonProcessingException {
        publisher.makeErrorResponse(paymentRequest, optimisticLockException);
        publisher.publish();

        ArgumentCaptor<PaymentResponse> responseCaptor = ArgumentCaptor.forClass(PaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        PaymentResponse expected = PaymentResponse.builder()
                .operationId(OPERATION_ID)
                .status(BALANCE_NOT_VERIFIED)
                .paymentResponseStatus(FAILED)
                .build();

        assertThat(responseCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
