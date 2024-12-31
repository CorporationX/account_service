package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
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
class ClearingPaymentPublisherTest {

    private static final UUID OPERATION_ID = UUID.randomUUID();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ClearingPaymentPublisher publisher;

    private Payment payment;
    private ClearingPaymentRequest clearingPaymentRequest;
    private Exception invalidPaymentStatusException;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(OPERATION_ID)
                .build();

        clearingPaymentRequest = ClearingPaymentRequest.builder()
                .operationId(OPERATION_ID)
                .build();

        invalidPaymentStatusException = new InvalidPaymentStatusException("");
    }

    @Test
    void testMakeResponse_successful() throws JsonProcessingException {
        publisher.makeResponse(payment);
        publisher.publish();

        ArgumentCaptor<ClearingPaymentResponse> captor = ArgumentCaptor.forClass(ClearingPaymentResponse.class);
        verify(objectMapper).writeValueAsString(captor.capture());

        ClearingPaymentResponse expected = ClearingPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(SUCCESS)
                .build();

        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void testMakeErrorResponse_successful() throws JsonProcessingException {
        publisher.makeErrorResponse(clearingPaymentRequest, invalidPaymentStatusException);
        publisher.publish();

        ArgumentCaptor<ClearingPaymentResponse> captor = ArgumentCaptor.forClass(ClearingPaymentResponse.class);
        verify(objectMapper).writeValueAsString(captor.capture());

        ClearingPaymentResponse expected = ClearingPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(FAILED)
                .build();

        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
