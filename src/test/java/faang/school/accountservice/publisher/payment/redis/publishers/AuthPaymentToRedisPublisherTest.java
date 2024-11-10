package faang.school.accountservice.publisher.payment.redis.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.response.AuthPaymentResponse;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.exception.ValidationException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static faang.school.accountservice.enums.payment.AccountBalanceStatus.BALANCE_NOT_VERIFIED;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.INSUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.payment.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.payment.PaymentStatus.FAILED;
import static faang.school.accountservice.enums.payment.PaymentStatus.SUCCESS;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildAuthPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthPaymentToRedisPublisherTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthPaymentToRedisPublisher publisher;

    @Test
    void testMakeResponse_successful() throws JsonProcessingException {
        AuthPayment authPayment = buildAuthPayment(OPERATION_ID);

        publisher.makeResponse(authPayment);
        publisher.publish();

        ArgumentCaptor<AuthPaymentResponse> responseCaptor = ArgumentCaptor.forClass(AuthPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        AuthPaymentResponse expectedAuthPaymentResponse = AuthPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .status(SUFFICIENT_FUNDS)
                .paymentStatus(SUCCESS)
                .build();
        AuthPaymentResponse authPaymentResponse = responseCaptor.getValue();
        assertThat(authPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthPaymentResponse);
    }

    @Test
    void testMakeErrorResponse_successful_validationException() throws JsonProcessingException {
        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest(OPERATION_ID);
        Exception exception = new ValidationException("");

        publisher.makeErrorResponse(authPaymentRequest, exception);
        publisher.publish();

        ArgumentCaptor<AuthPaymentResponse> responseCaptor = ArgumentCaptor.forClass(AuthPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        AuthPaymentResponse expectedAuthPaymentResponse = AuthPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .status(INSUFFICIENT_FUNDS)
                .paymentStatus(FAILED)
                .build();
        AuthPaymentResponse authPaymentResponse = responseCaptor.getValue();
        assertThat(authPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthPaymentResponse);
    }

    @Test
    void testMakeErrorResponse_successful_otherException() throws JsonProcessingException {
        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest(OPERATION_ID);
        Exception exception = new OptimisticLockException("");

        publisher.makeErrorResponse(authPaymentRequest, exception);
        publisher.publish();

        ArgumentCaptor<AuthPaymentResponse> responseCaptor = ArgumentCaptor.forClass(AuthPaymentResponse.class);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());

        AuthPaymentResponse expectedAuthPaymentResponse = AuthPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .status(BALANCE_NOT_VERIFIED)
                .paymentStatus(FAILED)
                .build();
        AuthPaymentResponse authPaymentResponse = responseCaptor.getValue();
        assertThat(authPaymentResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthPaymentResponse);
    }
}
