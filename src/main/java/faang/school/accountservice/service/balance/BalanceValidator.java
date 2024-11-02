package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.enums.pending.AccountBalanceStatus.INSUFFICIENT_FUNDS;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceValidator {
    private final PendingOperationStatusPublisher publisher;

    public void checkFreeAmount(OperationMessage operation, Balance balance, Money money) {
        double freeAmount = balance.getCurrentBalance().subtract(balance.getAuthBalance()).doubleValue();
        if (money.amount().doubleValue() > freeAmount) {
            publisher.publish(new CheckingAccountBalance(operation.getOperationId(), operation.getSourceAccountId(),
                    INSUFFICIENT_FUNDS));
            throw new ValidationException("Not enough funds, amount: %s, operation id: %s, balance id: %s",
                    money.amount(), operation.getOperationId(), balance.getId());
        }
    }

    public void checkAuthPaymentForAccept(Money money, AuthPayment payment) {
        if (!payment.getStatus().equals(ACTIVE)) {
            throw new ValidationException("AuthPayment with id: %s will not accepted, current status: %s",
                    payment.getId(), payment.getStatus());
        } else if ((money.amount().doubleValue() > payment.getAmount().doubleValue())) {
            throw new ValidationException("Money has amount more than limit: %s of payment with id: %s",
                    payment.getAmount(), payment.getId());
        }
    }

    public void checkAuthPaymentForReject(AuthPayment payment) {
        if (!payment.getStatus().equals(ACTIVE)) {
            throw new ValidationException("AuthPayment with id: %s will not rejected, current status: %s",
                    payment.getId(), payment.getStatus());
        }
    }
}
