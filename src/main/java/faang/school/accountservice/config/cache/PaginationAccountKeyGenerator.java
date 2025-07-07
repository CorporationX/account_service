package faang.school.accountservice.config.cache;

import faang.school.accountservice.dto.OwnerRequest;
import faang.school.accountservice.service.cache.OwnerCacheVersionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class PaginationAccountKeyGenerator implements KeyGenerator {
    private final OwnerCacheVersionService ownerCacheVersionService;

    @Override
    public @NonNull Object generate(@NonNull Object target, @NonNull Method method, Object... params) {
        OwnerRequest owner = (OwnerRequest) params[0];
        int blockNumber = (int) params[1];
        Sort sort = (Sort) params[2];
        String version = ownerCacheVersionService.getCurrentVersion(owner.getId(), owner.getType());
        return String.format("%d_%s_%s_%d_%s",
                owner.getId(),
                owner.getType().name(),
                version,
                blockNumber,
                sort.toString());
    }
}
