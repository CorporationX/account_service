package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.AuthPaymentStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BalanceValidator {
    public void checkFreeAmount(Balance balance, Money money) {
        double freeAmount = balance.getCurrentBalance().subtract(balance.getAuthBalance()).doubleValue();
        if (money.amount().doubleValue() > freeAmount) {
            throw new ValidationException("Not enough funds to authorize the amount: %s", money.amount());
        }
    }

    public void checkAuthPaymentForAccept(Money money, AuthPayment payment) {

        if (!(payment.getStatus() == (AuthPaymentStatus.ACTIVE))) {
            throw new ValidationException("AuthPayment with id : %s will not accepted, current status: %s",
                    payment.getId(), payment.getStatus());
        } else if (money.amount().doubleValue() > payment.getAmount().doubleValue()) {
            throw new ValidationException("Money has amount more than limit: %s of payment with id: %s",
                    payment.getAmount(), payment.getId());
        }
    }

    public void checkAuthPaymentForReject(AuthPayment payment) {
        if (!(payment.getStatus() == (AuthPaymentStatus.ACTIVE))) {
            throw new ValidationException("AuthPayment with id: %s will not rejected, current status: %s",
                    payment.getId(), payment.getStatus());
        }
    }
}
