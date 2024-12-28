package faang.school.accountservice.message;

import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClearingPaymentEventHandler {
    private final BalanceService balanceService;
    private final AccountRepository accountRepository;

    public void handle(AuthorizationEvent event) {
        // Получаем аккаунт отправителя
        Account senderAccount = accountRepository.findById(event.getSenderAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        // Получаем аккаунт получателя
        Account recipientAccount = accountRepository.findById(event.getRecipientAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

        Balance senderBalance = senderAccount.getBalance();
        Balance recipientBalance = recipientAccount.getBalance();

        balanceService.clearingPayment(senderBalance.getId(), recipientBalance.getId(), event.getAmount());
    }
}
