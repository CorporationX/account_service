package faang.school.accountservice.service.account.payment;

import faang.school.accountservice.dto.PaymentEventDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.PaymentOperation;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.mapper.PaymentOperationMapper;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.account.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AuthorizationPaymentHandler extends PaymentOperationHandler {
    private final AccountService accountService;
    private final PaymentOperationMapper mapper;

    public AuthorizationPaymentHandler(
            PaymentOperationService paymentOperationService,
            BalanceService balanceService,
            AccountService accountService,
            PaymentOperationMapper mapper) {
        super(paymentOperationService, balanceService);

        this.accountService = accountService;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public void handlePaymentOperation(PaymentEventDto paymentEventDto) {
        Long paymentNumber = paymentEventDto.getPaymentNumber();
        if (paymentOperationService.existsById(paymentNumber)) {
            log.error("Operation with number {} already has \"Authorization\" status in system", paymentNumber);
            return;
        }

        PaymentOperation pendingOperation = createPaymentOperation(paymentEventDto);

        balanceService.reservePaymentAmount(paymentEventDto.getDebitAccountId(), paymentEventDto.getAmount());

        paymentOperationService.saveOperation(pendingOperation);
    }

    private PaymentOperation createPaymentOperation(PaymentEventDto paymentEventDto) {
        Account debitAccount = accountService.getAccountModelById(paymentEventDto.getDebitAccountId());
        Account creditAccount = accountService.getAccountModelById(paymentEventDto.getCreditAccountId());

        PaymentOperation pendingOperation = mapper.toModel(paymentEventDto);
        pendingOperation.setDebitAccount(debitAccount);
        pendingOperation.setCreditAccount(creditAccount);
        pendingOperation.setIsCleared(false);

        return pendingOperation;
    }

    @Override
    public OperationType getRequiredOperationType() {
        return OperationType.AUTHORIZATION;
    }
}
