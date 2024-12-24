package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.enums.PaymentOperationType;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.publisher.PaymentMessageEventPublisher;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmPaymentStrategy implements PaymentOperationStrategy {
    private final BalanceRepository balanceRepository;
    private final BalanceService balanceService;
    private final PaymentMessageEventPublisher paymentMessageEventPublisher;


    @Override
    public void process(PaymentOperationDto payload, String correlationId) {
        log.info("Starting payment confirm processing. Payload: {}", payload);
        payload.setStatus(PaymentStatus.SUCCESS);
        payload.setOperationType(PaymentOperationType.CONFIRM);
        log.info("Continue payment confirm processing. Payload: {}", payload);

        PaymentDto initiateBasePaymentDto = PaymentDto.builder()
                .balanceOwnerId(balanceRepository.findLongByAccountId(payload.getOwnerAccId()))
                .balanceRecipientId(balanceRepository.findLongByAccountId(payload.getRecipientAccId()))
                .paymentOperationType(payload.getOperationType())
                .value(payload.getAmount())
                .build();

        try {
            balanceService.transferAmount(payload.getOwnerAccId(), initiateBasePaymentDto);
            log.info("Successfully updated balance for account: {}", payload.getOwnerAccId());
        } catch (Exception e) {
            log.error("Failed to update balance for account: {}. Error: {}",
                    payload.getOwnerAccId(), e.getMessage(), e);
            throw e;
        }

        try {
            paymentMessageEventPublisher.publishResponse(correlationId, payload);
            log.info("Successfully published payment response. CorrelationId: {}", correlationId);
        } catch (Exception e) {
            log.error("Failed to publish payment response. CorrelationId: {}. Error: {}",
                    correlationId, e.getMessage(), e);
            throw e;
        }
        log.info("Successfully published payment response. Payload: {}", payload);
    }
}