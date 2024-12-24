package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.publisher.PaymentMessageEventPublisher;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CancelPaymentStrategy implements PaymentOperationStrategy {
    private final BalanceRepository balanceRepository;
    private final BalanceService balanceService;
    private final PaymentMessageEventPublisher paymentMessageEventPublisher;

    @Override
    public void process(PaymentOperationDto payload, String correlationId) {
        log.info("Starting payment cancel processing. Payload: {}", payload);
        payload.setStatus(PaymentStatus.CANCELLED);
        log.info("Continue payment cancel processing. Payload: {}", payload);

        PaymentDto initiateBasePaymentDto = PaymentDto.builder()
                .balanceOwnerId(balanceRepository.findLongByAccountId(payload.getOwnerAccId()))
                .balanceRecipientId(balanceRepository.findLongByAccountId(payload.getRecipientAccId()))
                .paymentOperationType(payload.getOperationType())
                .value(payload.getAmount())
                .build();

        balanceService.update(payload.getOwnerAccId(), initiateBasePaymentDto);

        paymentMessageEventPublisher.publishResponse(
                correlationId,
                payload
        );
    }
}