package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.model.AuthPayment;
import faang.school.accountservice.model.AuthPaymentStatus;
import faang.school.accountservice.model.Balance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceValidator {
    public void checkFreeAmount(Balance balance, Money money) {
        double freeAmount = balance.getCurrentBalance().subtract(balance.getAuthBalance()).doubleValue();
        if (money.amount().doubleValue() > freeAmount) {
            throw new ValidationException("Not enough funds to authorize the amount: %s", money.amount());
        }
    }

    public void checkAuthPaymentForAccept(Money money, AuthPayment payment) {

        if (!payment.getStatus().equals(AuthPaymentStatus.ACTIVE)) {
            throw new ValidationException("AuthPayment with id : %s will not accepted, current status: %s",
                    payment.getId(), payment.getStatus());
        } else if (money.amount().doubleValue() > payment.getAmount().doubleValue()) {
            throw new ValidationException("Money has amount more than limit: %s of payment with id: %s",
                    payment.getAmount(), payment.getId());
        }
    }

    public void checkAuthPaymentForReject(AuthPayment payment) {
        if (!payment.getStatus().equals(AuthPaymentStatus.ACTIVE)) {
            throw new ValidationException("AuthPayment with id: %s will not rejected, current status: %s",
                    payment.getId(), payment.getStatus());
        }
    }
}
