package faang.school.accountservice.service.cache;

import faang.school.accountservice.model.OwnerType;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OwnerCacheVersionService {

    @Cacheable(value = "ownerAccountCacheVersion", key = "#ownerId + '_' + #ownerType.name()")
    public String getCurrentVersion(Long ownerId, OwnerType ownerType) {
        return UUID.randomUUID().toString();
    }

    @CachePut(value = "ownerAccountCacheVersion", key = "#ownerId + '_' + #ownerType.name()")
    public String invalidateAndGetNewVersion(Long ownerId, OwnerType ownerType) {
        return UUID.randomUUID().toString();
    }
}
