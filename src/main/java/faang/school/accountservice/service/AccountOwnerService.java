package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountOwnerRequest;
import faang.school.accountservice.dto.AccountOwnerResponse;
import faang.school.accountservice.dto.AccountOwnerWithAccountsResponse;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.mapper.AccountOwnerMapper;
import faang.school.accountservice.repository.AccountOwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOwnerService {

    private final AccountOwnerRepository accountOwnerRepository;
    private final AccountOwnerMapper accountOwnerMapper;

    @Transactional
    public AccountOwnerResponse createOwner(AccountOwnerRequest request) {
        log.info("Creating owner with ownerId: {}, ownerType: {}",
                request.getOwnerId(), request.getOwnerType());
        AccountOwner owner = AccountOwner.builder()
                .ownerId(request.getOwnerId())
                .ownerType(request.getOwnerType())
                .build();

        AccountOwner savedOwner = accountOwnerRepository.save(owner);
        log.info("Successfully created owner with ownerId: {}", savedOwner.getOwnerId());
        return accountOwnerMapper.toDto(savedOwner);
    }

    @Transactional(readOnly = true)
    public AccountOwnerWithAccountsResponse getOwnerWithAccountsByOwnerIdAndType(Long ownerId, OwnerType ownerType) {
        AccountOwner owner = accountOwnerRepository.findByOwnerIdAndOwnerType(ownerId, ownerType)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        return accountOwnerMapper.toOwnerWithAccountsDto(owner);
    }
}
