package faang.school.accountservice.service.status;

import faang.school.accountservice.enums.AccountStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class AccountStatusActions {

    private final Map<AccountStatus, List<AccountStatus>> availableStatusActions = new EnumMap<>(AccountStatus.class);

    public AccountStatusActions() {
        availableStatusActions.put(AccountStatus.ACTIVE, List.of(AccountStatus.FROZEN, AccountStatus.CLOSED));
        availableStatusActions.put(AccountStatus.FROZEN, List.of(AccountStatus.ACTIVE, AccountStatus.CLOSED));
        availableStatusActions.put(AccountStatus.CLOSED, List.of(AccountStatus.ACTIVE));
    }

    public List<AccountStatus> getAvailableActions(AccountStatus currentStatus) {
        List<AccountStatus> actions = availableStatusActions.get(currentStatus);
        if (actions == null) {
            throw new NoSuchElementException("Status " + this + " is unknown");
        }
        return actions;
    }
}
