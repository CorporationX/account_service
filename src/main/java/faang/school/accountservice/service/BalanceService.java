package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.UnsupportedOperationException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceValidator;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceValidator balanceValidator;

    @Transactional
    public BalanceDto create(BalanceDto balanceDto) {
        balanceValidator.checkBalanceToCreate(balanceDto);
        Balance createdBalance = balanceRepository.create(
                balanceDto.getAuthorizedBalance(),
                balanceDto.getActualBalance(),
                balanceDto.getCurrency().toString(),
                balanceDto.getAccountId());
        return balanceMapper.toDto(createdBalance);
    }
    @Transactional
    public BalanceDto getById(UUID id) {
        return balanceMapper.toDto(getEntityById(id));
    }

    @Transactional
    public Balance getEntityById(UUID id) {
        return balanceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Balance with %s id doesn't exist", id)));
    }

    @Transactional
    public BalanceDto getByAccountId(Long accountId) {
        Balance foundedBalance = balanceRepository.getByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Balances by account id [%s] doesn't exist", accountId)));

        return balanceMapper.toDto(foundedBalance);
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public BalanceDto update(BalanceDto balanceDto) {
        try {
            balanceValidator.checkBalanceToUpdate(balanceDto);

            Balance balanceToUpdate = getEntityById(balanceDto.getId());
            balanceMapper.updateBalance(balanceDto, balanceToUpdate);

            Balance updatedBalance = balanceRepository.save(balanceToUpdate);

            return balanceMapper.toDto(updatedBalance);
        } catch (OptimisticLockException e) {
            log.error("Update error balance with id {}: {}", balanceDto.getId(), e.getCause());
            throw new UnsupportedOperationException(e.getMessage());
        }
    }
}
