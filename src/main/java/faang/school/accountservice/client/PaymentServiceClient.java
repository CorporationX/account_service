package faang.school.accountservice.client;

import faang.school.accountservice.dto.payment.PaymentRequestDto;
import faang.school.accountservice.dto.payment.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.api-url}")
public interface PaymentServiceClient {

    @Retryable(
            maxAttemptsExpression = "#{@paymentRetryConfig.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@paymentRetryConfig.initialDelay}",
                    multiplierExpression = "#{@paymentRetryConfig.multiplier}"
            )
    )
    @PostMapping("/api/payment")
    PaymentResponseDto sendPayment(@RequestBody PaymentRequestDto paymentRequest);
}
