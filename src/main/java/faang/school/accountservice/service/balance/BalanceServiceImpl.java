package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.PaymentAccount;
import faang.school.accountservice.entity.PendingOperation;
import faang.school.accountservice.exception.UnauthorizedAccessException;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.PaymentAccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.audit.BalanceAuditService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;

    private final BalanceRepository balanceRepository;
    private final PaymentAccountRepository accountRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceAuditService balanceAuditService;

    @Override
    public BalanceDto getBalance(Long balanceId, Long userId) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(
                () -> {
                    log.error("Balance with id: {} not found", balanceId);
                    return new EntityNotFoundException(String.format("Balance with id: %d not found", balanceId));
                });
        checkPermissions(balance, userId);
        log.info("Successfully retrieved balance with id: {}", balanceId);

        return balanceMapper.toDto(balance);
    }

    @Transactional
    @Override
    public BalanceDto createBalance(Long accountId) {
        Balance balance = initializeBalance(accountId);
        BalanceDto balanceDto = balanceMapper.toDto(balanceRepository.save(balance));
        log.info("Successfully created balance for account with id: {}", accountId);
//        var audit = balanceAuditService.saveAudit(balance);
//        log.info("Successfully updated audit for balance with id: {}", audit.getId());
        return balanceDto;
    }

    @Transactional
    @Override
    public BalanceDto updateBalance(Long balanceId, BalanceDto balanceDto, PendingOperation operation) {
        try {
            Balance balance = balanceRepository.findById(balanceId).orElseThrow(
                    () -> {
                        log.error("Balance with id: {} not found", balanceId);
                        return new EntityNotFoundException(String.format("Balance with id: %d not found", balanceId));
                    });
            balance.setAuthorizedBalance(balanceDto.getAuthorizedBalance());
            balance.setActualBalance(balanceDto.getActualBalance());
            log.info("Successfully updated balance for account with id: {}", balanceId);
            var updatedBalance = balanceRepository.save(balance);

            var audit = balanceAuditService.saveAudit(updatedBalance, operation);
            log.info("Successfully updated audit for balance with id: {}", audit.getId());

            return balanceMapper.toDto(updatedBalance);
        } catch (OptimisticLockException ex) {
            log.error("Optimistic lock exception occurred for balance with id: {}", balanceId, ex);
            throw new ConcurrentModificationException("The balance was modified by another transaction", ex);
        }
    }

    private Balance initializeBalance(Long accountId) {
        Balance balance = new Balance();
        PaymentAccount account = accountRepository.findById(accountId).orElseThrow(
                () -> {
                    log.error("Account with id: {} not found", accountId);
                    return new EntityNotFoundException(String.format("Account with id: %d not found", accountId));
                }
        );
        balance.setAccount(account);
        balance.setAuthorizedBalance(INITIAL_BALANCE);
        balance.setActualBalance(INITIAL_BALANCE);
        return balance;
    }

    private void checkPermissions(Balance balance, Long userId) {
        Long accountOwnerId = balance.getAccount().getUserId();
        if (!accountOwnerId.equals(userId)) {
            throw new UnauthorizedAccessException("User is not authorized to access this resource");
        }
    }
}
