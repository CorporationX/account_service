package faang.school.accountservice.publisher.payment.redis.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.response.ErrorPaymentResponse;
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
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildErrorPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ErrorPaymentToRedisPublisherTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ErrorPaymentToRedisPublisher publisher;

    @Test
    void testMakeResponse_successful() throws JsonProcessingException {
        AuthPayment authPayment = buildAuthPayment(OPERATION_ID);

        publisher.makeResponse(authPayment);
        publisher.publish();

        ArgumentCaptor<ErrorPaymentResponse> responseCaptor = ArgumentCaptor.forClass(ErrorPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        ErrorPaymentResponse expectedPaymentResponse = ErrorPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentStatus(SUCCESS)
                .build();
        ErrorPaymentResponse paymentResponse = responseCaptor.getValue();
        assertThat(paymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentResponse);
    }

    @Test
    void testMakeErrorResponse_successful() throws JsonProcessingException {
        ErrorPaymentRequest paymentRequest = buildErrorPaymentRequest(OPERATION_ID);
        Exception exception = new ValidationException("");

        publisher.makeErrorResponse(paymentRequest, exception);
        publisher.publish();

        ArgumentCaptor<ErrorPaymentResponse> responseCaptor = ArgumentCaptor.forClass(ErrorPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        ErrorPaymentResponse expectedPaymentResponse = ErrorPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentStatus(FAILED)
                .build();
        ErrorPaymentResponse paymentResponse = responseCaptor.getValue();
        assertThat(paymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentResponse);
    }
}
