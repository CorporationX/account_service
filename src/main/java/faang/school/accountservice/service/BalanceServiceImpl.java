package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.publisher.PaymentStatusChangePublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceJpaRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper mapper;
    private final BalanceAuditMapper auditMapper;
    private final PaymentStatusChangePublisher paymentStatusChangePublisher;

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
        Balance fromBalance = balanceRepository.findBalanceByAccount_Id(pendingDto.getFromAccountId());
        Balance toBalance = balanceRepository.findBalanceByAccount_Id(pendingDto.getToAccountId());
        double amount = pendingDto.getAmount().doubleValue();

        if (fromBalance.getCurAuthBalance() < amount) {
            pendingDto.setStatus(PendingStatus.CANCELED);
            paymentStatusChangePublisher.publish(pendingDto);
            log.info("Payment authorization failed");
            return;
        }

        fromBalance.setCurAuthBalance(fromBalance.getCurAuthBalance() - amount);
        toBalance.setCurAuthBalance(toBalance.getCurAuthBalance() + amount);

        balanceRepository.save(fromBalance);
        balanceRepository.save(toBalance);
        log.info("Payment authorization successful");
    }

    @Override
    @Transactional
    public void clearPayment(List<PendingDto> pendingDtos) {
        for (PendingDto pendingDto : pendingDtos) {
            submitPayment(pendingDto);
        }
    }

    private void submitPayment(PendingDto pendingDto) {
        Balance fromBalance = balanceRepository.findBalanceByAccount_Id(pendingDto.getFromAccountId());
        Balance toBalance = balanceRepository.findBalanceByAccount_Id(pendingDto.getToAccountId());
        double amount = pendingDto.getAmount().doubleValue();

        fromBalance.setCurFactBalance(fromBalance.getCurFactBalance() - amount);
        toBalance.setCurFactBalance(toBalance.getCurFactBalance() + amount);

        balanceRepository.save(fromBalance);
        balanceRepository.save(toBalance);

        pendingDto.setStatus(PendingStatus.SUCCESS);
        paymentStatusChangePublisher.publish(pendingDto);
        log.info("Сlearing was successful");
    }

    @Override
    public BalanceDto getBalance(long accountId) {
        return mapper.toDto(balanceRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Not found account with id = " + accountId)));
    }
}
