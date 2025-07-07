package faang.school.accountservice.service.cache;

import faang.school.accountservice.dto.AccountPreviewDto;
import faang.school.accountservice.dto.OwnerRequest;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountCacheableFetcher {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Value("${cache.key.pagination.block-size}")
    private int blockSize;

    @Cacheable(value = "accountByOwnerId", keyGenerator = "paginationAccountKeyGenerator")
    @Transactional(readOnly = true)
    public List<AccountPreviewDto> fetchAccountBlock(OwnerRequest owner, int blockNumber, Sort sort) {
        log.info("CACHE MISS: Fetching block {} for ownerId {}, ownerType: {} from database",
                blockNumber, owner.getId(), owner.getType());
        if (!accountRepository.existsByOwnerIdAndOwnerType(owner.getId(), owner.getType())) {
            log.warn("No accounts found for ownerId: {}, ownerType: {}", owner.getId(), owner.getType());
            throw new AccountNotFoundException(owner.getId(), owner.getType().name());
        }
        Pageable pageable = PageRequest.of(blockNumber, blockSize, sort);
        return accountMapper.toPreviewDto(accountRepository
                        .findByOwnerIdAndOwnerType(owner.getId(), owner.getType(), pageable));
    }
}
