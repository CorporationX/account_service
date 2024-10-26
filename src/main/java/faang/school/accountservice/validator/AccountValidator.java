package faang.school.accountservice.validator;

import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.enums.AccountStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountValidator {

    public void validateAccountToBlock(Account account, long ownerId) {
        if (account.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("Only owner can block this account");
        }
        if (account.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new IllegalArgumentException("This account is already blocked");
        }
    }

    public void validateAccountToClose(Account account, long ownerId) {
        if (account.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("Only owner can close this account");
        }
        if (!account.getBalance().getCurrentActualBalance().equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("This account has money on its balance");
        }
    }
}
