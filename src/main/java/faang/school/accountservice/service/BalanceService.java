package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.RequestBalanceAuditDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountService accountService;
    private final BalanceMapper balanceMapper;
    private final BalanceAuditService balanceAuditService;

    private Balance getBalanceByIdForUpdate(Long balanceId) {
        return balanceRepository.findByIdForUpdate(balanceId).orElseThrow(() ->
                new EntityNotFoundException("Balance with id = " + balanceId + "not found"));
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalanceById(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(() ->
                new EntityNotFoundException("Balance with id = " + balanceId + "not found"));
        return balanceMapper.toDto(balance);
    }


    @Transactional
    public BalanceDto createBalance(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        Balance balance = Balance.builder()
                .account(account)
                .build();

        account.setBalance(balance);

        balanceRepository.save(balance);

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto plusBalance(Long balanceId, Double count, String operationId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        BigDecimal newActualBalance = balance.getActualBalance().add(BigDecimal.valueOf(count));

        balance.setActualBalance(newActualBalance);

        balanceAuditService.createAudit(new RequestBalanceAuditDto(balance.getAccount().getId(), operationId));

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto authBalance(Long balanceId, Double count, String operationId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance().compareTo(BigDecimal.valueOf(0.)) > 0) {
            throw new IllegalArgumentException("There is already money reserved");
        }

        BigDecimal newActualBalance = balance.getActualBalance().subtract(BigDecimal.valueOf(count));
        balance.setActualBalance(newActualBalance);
        balance.setAuthBalance(BigDecimal.valueOf(count));

        log.info("________________________________________________________________________Auth Balance is created. BalanceId: {}", balanceId);

        balanceAuditService.createAudit(new RequestBalanceAuditDto(balance.getAccount().getId(), operationId));

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto clearingBalance(Long balanceId, String operationId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance().compareTo(BigDecimal.valueOf(0.)) == 0) {
            throw new IllegalArgumentException("Auth Balance is 0");
        }

        balance.setAuthBalance(BigDecimal.valueOf(0.));

        balanceAuditService.createAudit(new RequestBalanceAuditDto(balance.getAccount().getId(), operationId));

        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto clearingBalance(Long balanceId, Double count, String operationId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance().compareTo(BigDecimal.valueOf(0.)) == 0) {
            throw new IllegalArgumentException("Auth Balance is 0");
        }

        if (balance.getAuthBalance().compareTo(BigDecimal.valueOf(count)) < 0) {
            throw new IllegalArgumentException("Auth balance is less than required");
        }

        BigDecimal newActualBalance = balance.getActualBalance().add(balance.getAuthBalance());
        balance.setActualBalance(newActualBalance.subtract(BigDecimal.valueOf(count)));
        balance.setAuthBalance(BigDecimal.valueOf(0.));

        balanceAuditService.createAudit(new RequestBalanceAuditDto(balance.getAccount().getId(), operationId));

        return balanceMapper.toDto(balance);
    }


    @Transactional
    public BalanceDto cancelBalance(Long balanceId, String operationId) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        if (balance.getAuthBalance().compareTo(BigDecimal.valueOf(0.)) == 0) {
            throw new IllegalArgumentException("Cancellation is not possible");
        }

        BigDecimal newActualBalance = balance.getActualBalance().add(balance.getAuthBalance());
        balance.setActualBalance(newActualBalance);
        balance.setAuthBalance(BigDecimal.valueOf(0.));

        balanceAuditService.createAudit(new RequestBalanceAuditDto(balance.getAccount().getId(), operationId));

        return balanceMapper.toDto(balance);
    }
}