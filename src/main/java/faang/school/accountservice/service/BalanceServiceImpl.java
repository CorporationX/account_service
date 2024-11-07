package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.publisher.PaymentStatusChangePublisher;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceJpaRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceMapper mapper;
    private final BalanceAuditMapper auditMapper;
    private final PaymentStatusChangePublisher paymentStatusChangePublisher;
    private final ApplicationContext applicationContext;

    @Override
    public void create(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setVersion(1);

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    @Transactional
    public void paymentAuthorization(@Validated PendingDto pendingDto) {
        Balance fromBalance = findBalanceByAccountId(pendingDto.getFromAccountId());
        Balance toBalance = findBalanceByAccountId(pendingDto.getToAccountId());
        BigDecimal amount = pendingDto.getAmount();

        if (fromBalance.getCurAuthBalance().compareTo(amount) < 0) {
            pendingDto.setStatus(PendingStatus.CANCELED);
            paymentStatusChangePublisher.publish(pendingDto);
            log.warn("Payment authorization failed");
            return;
        }

        fromBalance.setCurAuthBalance(fromBalance.getCurAuthBalance().subtract(amount));
        toBalance.setCurAuthBalance(toBalance.getCurAuthBalance().add(amount));

        balanceRepository.save(fromBalance);
        balanceRepository.save(toBalance);
        log.info("Payment authorization successful");
    }

    @Override
    public void clearPayment(List<PendingDto> pendingDtos) {
        pendingDtos.forEach(pendingDto -> {
            BalanceService self = applicationContext.getBean(this.getClass());
            self.clearPayment(pendingDto);
        });
    }

    @Override
    @Transactional
    @Async("mainExecutorService")
    public void clearPayment(@Validated PendingDto pendingDto) {
        Balance fromBalance = findBalanceByAccountId(pendingDto.getFromAccountId());
        Balance toBalance = findBalanceByAccountId(pendingDto.getToAccountId());
        BigDecimal amount = pendingDto.getAmount();

        fromBalance.setCurFactBalance(fromBalance.getCurFactBalance().subtract(amount));
        toBalance.setCurFactBalance(toBalance.getCurFactBalance().add(amount));

        balanceRepository.save(fromBalance);
        balanceRepository.save(toBalance);

        pendingDto.setStatus(PendingStatus.SUCCESS);
        paymentStatusChangePublisher.publish(pendingDto);
        log.info("Сlearing was successful");
    }

    @Override
    public BalanceDto getBalance(long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Not found account with id = " + accountId));
    }

    @Override
    public BalanceDto getBalanceByAccountId(long accountId) {
        Balance balance = findBalanceByAccountId(accountId);
        return mapper.toDto(balance);
    }

    private Balance findBalanceByAccountId(long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance with account id %d not found".formatted(accountId)));
    }
}
