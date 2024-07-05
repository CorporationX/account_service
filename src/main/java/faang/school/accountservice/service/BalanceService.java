package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceValidator balanceValidator;
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional(readOnly = true)
    public BalanceDto getBalance(Long balanceId) {
        balanceValidator.checkIsNull(balanceId);
        return balanceMapper.toDto(findById(balanceId));
    }

    @Transactional
    public BalanceDto createBalance(@Valid @NotNull BalanceDto balanceDto, @Positive long operationId) {
        balanceValidator.checkAbsenceBalanceByAccountIdInBd(balanceDto);

        Balance balance = balanceMapper.toEntity(balanceDto);
        Balance updateBalance = balanceRepository.save(balance);

        BalanceAudit balanceAudit = balanceMapper.toBalanceAudit(updateBalance, operationId);
        balanceAuditRepository.save(balanceAudit);

        return balanceMapper.toDto(updateBalance);
    }

    @Transactional
    public BalanceDto updateBalance(@Valid @NotNull BalanceDto balanceDto, @Positive long operationId) {
        balanceValidator.checkExistsBalanceByIdInBd(balanceDto);

        Balance balance = findById(balanceDto.getId());
        balanceMapper.updateBalanceFromDto(balanceDto, balance);

        Balance updateBalance = balanceRepository.save(balance);
        BalanceAudit balanceAudit = balanceMapper.toBalanceAudit(updateBalance, operationId);

        balanceAuditRepository.save(balanceAudit);

        return balanceMapper.toDto(updateBalance);
    }

    private Balance findById(long id) {
        return balanceRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("The balance with id " + id + " is not in the database"));
    }
}
