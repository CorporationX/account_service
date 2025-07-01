package faang.school.accountservice.service.cache;

import faang.school.accountservice.model.OwnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class OwnerCacheVersionService {

    @Cacheable(value = "ownerAccountCacheVersion", key = "#ownerId + '_' + #ownerType.name()")
    public String getCurrentVersion(Long ownerId, OwnerType ownerType) {
        log.info("New cache version for ownerId: {}, ownerType: {}", ownerId, ownerType);
        return UUID.randomUUID().toString();
    }

    @CacheEvict(value = "ownerAccountCacheVersion", key = "#ownerId + '_' + #ownerType.name()")
    public void invalidateCache(Long ownerId, OwnerType ownerType) {
        log.info("Invalidating cache for ownerId: {}, ownerType: {}", ownerId, ownerType);
    }
}
