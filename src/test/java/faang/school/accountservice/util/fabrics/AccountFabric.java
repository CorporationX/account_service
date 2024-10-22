package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.model.Account;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AccountFabric {
    public static Account buildAccount() {
        return Account.builder()
                .build();
    }

    public static Account buildAccount(UUID id) {
        return Account.builder()
                .id(id)
                .build();
    }
}
