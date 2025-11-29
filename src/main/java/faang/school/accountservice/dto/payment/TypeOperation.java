package faang.school.accountservice.dto.payment;

import faang.school.accountservice.service.BalanceService;

import java.util.function.BiConsumer;

public enum TypeOperation {
    PENDING((service, payment) -> service.authorize(payment.accountId(), payment.amount())),
    WRITING((service, payment) -> service.clearing(payment.accountId(), payment.amount())),
    CANCELING((service, payment) -> service.cancelAuthorization(payment.accountId(), payment.amount()));

    private final BiConsumer<BalanceService,PaymentDto> handler;

    TypeOperation(BiConsumer<BalanceService,PaymentDto> handler) {
        this.handler = handler;
    }

    public void process(BalanceService balanceService, PaymentDto paymentDto) {
        handler.accept(balanceService, paymentDto);
    }
}
