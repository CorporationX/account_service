package faang.school.accountservice.service;

import faang.school.accountservice.dto.PaymentResultDto;
import faang.school.accountservice.exception.AccountInactiveException;
import faang.school.accountservice.exception.InsufficientFundsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountService accountService;
    private final BalanceService balanceService;

    public PaymentResultDto paymentProcess(String accountNumber, BigDecimal amount) {
        try {
            accountService.accountAvailable(accountNumber);
            balanceService.authorizePayment(accountNumber, amount);
            balanceService.clearingPayment(accountNumber, amount);
            return getPaymentResultDto(true, "Payment was successful!");
        } catch (EntityNotFoundException e) {
            return getPaymentResultDto(false, "Account not found!");
        } catch (AccountInactiveException e) {
            return getPaymentResultDto(false, "Account not active!");
        } catch (InsufficientFundsException e) {
            return getPaymentResultDto(false, "Insufficient funds for payment!");
        } catch (Exception e) {
            balanceService.cancelAuthorization(accountNumber, amount);
            return getPaymentResultDto(false, "Payment error!");
        }
    }

    private static PaymentResultDto getPaymentResultDto(boolean success, String message) {
        return PaymentResultDto.builder()
                .success(success)
                .message(message)
                .build();
    }

}
