package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.exception.payment.InvalidPaymentStatusException;
import faang.school.accountservice.model.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static faang.school.accountservice.model.payment.PaymentResponseStatus.FAILED;
import static faang.school.accountservice.model.payment.PaymentResponseStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelPaymentPublisherTest {

    private static final UUID OPERATION_ID = UUID.randomUUID();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CancelPaymentPublisher publisher;

    private Payment payment;
    private CancelPaymentRequest paymentRequest;
    private Exception invalidPaymentStatusException;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(OPERATION_ID)
                .build();

        paymentRequest = CancelPaymentRequest.builder()
                .operationId(OPERATION_ID)
                .build();

        invalidPaymentStatusException = new InvalidPaymentStatusException("");
    }

    @Test
    void testMakeResponse_successful() throws JsonProcessingException {
        publisher.makeResponse(payment);
        publisher.publish();

        ArgumentCaptor<CancelPaymentResponse> responseCaptor =
                ArgumentCaptor.forClass(CancelPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        CancelPaymentResponse expectedPaymentResponse = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(SUCCESS)
                .build();

        assertThat(responseCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentResponse);
    }

    @Test
    void testMakeErrorResponse_successful() throws JsonProcessingException {
        publisher.makeErrorResponse(paymentRequest, invalidPaymentStatusException);
        publisher.publish();

        ArgumentCaptor<CancelPaymentResponse> responseCaptor =
                ArgumentCaptor.forClass(CancelPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        CancelPaymentResponse expectedPaymentResponse = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(FAILED)
                .build();

        assertThat(responseCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentResponse);
    }
}

