package faang.school.accountservice.service.handler;

import faang.school.accountservice.client.PaymentServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.payment.PaymentRequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class TransferRequestHandler implements RequestHandler {

    private static final String INPUT_DATA_AMOUNT = "amount";

    private final PaymentServiceClient paymentServiceClient;
    private final UserContext userContext;

    @Override
    public RequestType getType() {
        return RequestType.TRANSFER;
    }

    @Override
    public void handle(Request request) {
        PaymentRequestDto paymentRequest = buildPaymentRequest(request);
        Long userId = request.getUserId();
        try {
            userContext.setUserId(userId);
            paymentServiceClient.sendPayment(paymentRequest);
            log.info("Payment successful");
        } catch (Exception e) {
            log.error("Payment failed after retries: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed", e);
        } finally {
            userContext.clear();
        }
    }

    private PaymentRequestDto buildPaymentRequest(Request request) {
        return PaymentRequestDto.builder()
                .paymentNumber(request.getUserId())
                .amount(new BigDecimal(String.valueOf(request.getInputData().get(INPUT_DATA_AMOUNT))))
                .currency(Currency.USD)
                .build();
    }
}
