package faang.school.accountservice.service.status;

import faang.school.accountservice.enums.AccountStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class AccountStatusManager {

    private final Map<AccountStatus, List<AccountStatus>> availableStatusActions = new EnumMap<>(AccountStatus.class);

    public AccountStatusManager() {
        availableStatusActions.put(AccountStatus.ACTIVE, List.of(AccountStatus.FROZEN, AccountStatus.CLOSED));
        availableStatusActions.put(AccountStatus.FROZEN, List.of(AccountStatus.ACTIVE, AccountStatus.CLOSED));
        availableStatusActions.put(AccountStatus.CLOSED, List.of(AccountStatus.ACTIVE));
    }

    public List<AccountStatus> getAvailableActions(AccountStatus currentStatus) {
        List<AccountStatus> actions = availableStatusActions.get(currentStatus);
        if (actions == null) {
            throw new NoSuchElementException("Status " + currentStatus + " is unknown");
        }
        return actions;
    }

    public boolean isStatusAvailableForChange(AccountStatus currentStatus, AccountStatus statusForUpdate){
        List<AccountStatus> actions = availableStatusActions.get(currentStatus);
        return actions != null && actions.contains(statusForUpdate);
    }
}
