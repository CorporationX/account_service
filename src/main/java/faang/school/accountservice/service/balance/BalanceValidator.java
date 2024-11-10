package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceValidator {
    public void checkFreeAmount(UUID operationId, Balance sourceBalance, Money money) {
        if (money.amount().doubleValue() > sourceBalance.getCurrentBalance().doubleValue()) {
            throw new ValidationException("Not enough funds, amount: %s, operation id: %s, balance id: %s",
                    money.amount(), operationId, sourceBalance.getId());
        }
    }

    public void checkAuthPaymentForAccept(AuthPayment payment) {
        if (!payment.getStatus().equals(ACTIVE)) {
            throw new ValidationException("AuthPayment with id: %s will not accepted, current status: %s",
                    payment.getId(), payment.getStatus());
        }
    }

    public void checkAuthPaymentForReject(AuthPayment payment) {
        if (!payment.getStatus().equals(ACTIVE)) {
            throw new ValidationException("AuthPayment with id: %s will not rejected, current status: %s",
                    payment.getId(), payment.getStatus());
        }
    }
}
