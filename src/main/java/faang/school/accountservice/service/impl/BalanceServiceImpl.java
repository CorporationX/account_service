package faang.school.accountservice.service.impl;

import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.BalanceService;

import java.time.LocalDateTime;
import java.util.UUID;

import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.dto.BalanceDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.util.ExceptionThrowingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final ExceptionThrowingValidator validator;
    private final BalanceAuditRepository balanceAuditRepository;

    public BalanceDto getBalanceByAccountId(Long accountId) {
        Optional<Balance> optionalBalance = balanceRepository.findByAccountId(accountId);
        if (optionalBalance.isEmpty()) {
            throw new RuntimeException("Balance not found for account id: " + accountId);
        }
        Balance balance = optionalBalance.get();
        BalanceDto balanceDto = balanceMapper.toDto(balance);
        validator.validate(balanceDto, BalanceDto.GetResponse.class);
        return balanceDto;
    }
    @Transactional
    public BalanceDto updateBalance(BalanceDto balanceToUpdateDto) {
        Long id = balanceToUpdateDto.getId();
        Optional<Balance> optionalBalance = balanceRepository.findById(id);
        if (optionalBalance.isEmpty()) {
            throw new RuntimeException("Balance not found for id: " + id);
        }
        BigDecimal newAuthorizedBalance = balanceToUpdateDto.getAuthorizedBalance();
        BigDecimal newActualBalance = balanceToUpdateDto.getActualBalance();
        if (newActualBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Actual balance cannot be negative.");
        }
        if (newAuthorizedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Authorized balance cannot be negative.");
        }
        if (newAuthorizedBalance.compareTo(newActualBalance) > 0) {
            throw new RuntimeException("Authorized balance cannot exceed actual balance.");
        }
        Balance currentBalance = optionalBalance.get();
        currentBalance.setAuthorizedBalance(balanceToUpdateDto.getAuthorizedBalance());
        currentBalance.setActualBalance(balanceToUpdateDto.getActualBalance());
        Balance updatedBalance = balanceRepository.save(currentBalance);

        BalanceAudit audit = balanceMapper.toBalanceAudit(updatedBalance, UUID.randomUUID());
        balanceAuditRepository.save(audit);

        BalanceDto updatedBalanceDto = balanceMapper.toDto(updatedBalance);
        validator.validate(updatedBalanceDto, BalanceDto.GetResponse.class);
        return updatedBalanceDto;
    }
}