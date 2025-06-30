package faang.school.accountservice.listener;

import faang.school.accountservice.AccountCreatedEvent;
import faang.school.accountservice.service.cache.OwnerCacheVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationListener {
    private final OwnerCacheVersionService ownerCacheVersionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccountCreated(AccountCreatedEvent event) {
        log.info("Transaction committed for new account. Invalidating cache for ownerId: {}, ownerType: {}",
                event.ownerId(), event.ownerType());
        ownerCacheVersionService.invalidateAndGetNewVersion(event.ownerId(), event.ownerType());
    }
}
