package faang.school.accountservice.publisher.payment.redis.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static faang.school.accountservice.enums.payment.PaymentStatus.FAILED;
import static faang.school.accountservice.enums.payment.PaymentStatus.SUCCESS;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildCancelPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelPaymentToRedisPublisherTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CancelPaymentToRedisPublisher publisher;

    @Test
    void testMakeResponse_successful() throws JsonProcessingException {
        AuthPayment authPayment = buildAuthPayment(OPERATION_ID);

        publisher.makeResponse(authPayment);
        publisher.publish();

        ArgumentCaptor<CancelPaymentResponse> responseCaptor = ArgumentCaptor.forClass(CancelPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        CancelPaymentResponse expectedPaymentResponse = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentStatus(SUCCESS)
                .build();
        CancelPaymentResponse paymentResponse = responseCaptor.getValue();
        assertThat(paymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentResponse);
    }

    @Test
    void testMakeErrorResponse_successful() throws JsonProcessingException {
        CancelPaymentRequest paymentRequest = buildCancelPaymentRequest(OPERATION_ID);
        Exception exception = new ValidationException("");

        publisher.makeErrorResponse(paymentRequest, exception);
        publisher.publish();

        ArgumentCaptor<CancelPaymentResponse> responseCaptor = ArgumentCaptor.forClass(CancelPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        CancelPaymentResponse expectedPaymentResponse = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentStatus(FAILED)
                .build();
        CancelPaymentResponse paymentResponse = responseCaptor.getValue();
        assertThat(paymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentResponse);
    }
}
