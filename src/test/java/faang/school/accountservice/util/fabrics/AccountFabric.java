package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

import static faang.school.accountservice.enums.account.AccountType.DEBIT;
import static faang.school.accountservice.enums.payment.Currency.USD;
import static faang.school.accountservice.enums.account.AccountStatus.ACTIVE;

@UtilityClass
public class AccountFabric {
    private static final String ACCOUNT_NUMBER = "408124878517";

    public static Account buildAccountDefault(Long userId) {
        return Account.builder()
                .number(ACCOUNT_NUMBER)
                .userId(userId)
                .type(DEBIT)
                .currency(USD)
                .status(ACTIVE)
                .closedAt(LocalDateTime.now())
                .version(0)
                .build();
    }

    public static Account buildAccount() {
        return Account.builder()
                .build();
    }

    public static Account buildAccount(UUID id) {
        return Account.builder()
                .id(id)
                .build();
    }

    public static Account buildAccount(Balance balance) {
        return Account.builder()
                .balance(balance)
                .build();
    }
}
