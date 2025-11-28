package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.ChangedBalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.InvalidBalanceOperationException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validation.BalanceValidator;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceValidator balanceValidator;

    @Override
    @Transactional
    public BalanceDto create(CreateBalanceDto createBalanceDto) {
        Account account = balanceValidator.validateAccountExisting(createBalanceDto.accountId());
        log.debug("Account {} has been found", account);
        Balance balance = balanceMapper.toBalance(createBalanceDto);
        balance.setAccount(account);
        balance = balanceRepository.save(balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    @Transactional
    public BalanceDto update(long balanceId, UpdateBalanceDto updateBalanceDto) {
        Balance balance = balanceValidator.validateBalanceExisting(balanceId);
        log.debug("Balance {} has been found", balance);
        balanceMapper.update(updateBalanceDto, balance);
        if (updateBalanceDto.accountId() != null) {
            Account account = balanceValidator.validateAccountExisting(updateBalanceDto.accountId());
            balance.setAccount(account);
        }
        return saveAndMap(balance, "updating");
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceDto getBalanceById(long balanceId) {
        Balance balance = balanceValidator.validateBalanceExisting(balanceId);
        log.debug("Balance {} has been found", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    @Transactional
    public BalanceDto authorize(long balanceId, ChangedBalanceDto authorizeBalanceDto) {
        Balance balance = balanceValidator.validateBalanceExisting(balanceId);
        log.debug("Balance {} has been found", balance);
        balanceValidator.validateAmount(authorizeBalanceDto.amount());
        balanceValidator.validateEnoughActualBalance(balance, authorizeBalanceDto.amount());
        balance.setAuthorizationBalance(balance
                .getAuthorizationBalance()
                .add(authorizeBalanceDto.amount()));
        balance.setActualBalance(balance
                .getActualBalance()
                .subtract(authorizeBalanceDto.amount()));
        return saveAndMap(balance, "making an authorization");
    }

    @Override
    @Transactional
    public BalanceDto confirm(long balanceId, ChangedBalanceDto confirmBalanceDto) {
        Balance balance = balanceValidator.validateBalanceExisting(balanceId);
        log.debug("Balance {} has been found", balance);
        balanceValidator.validateAmount(confirmBalanceDto.amount());
        balanceValidator.validateEnoughAuthorizationBalance(balance, confirmBalanceDto.amount());
        balance.setAuthorizationBalance(balance
                .getAuthorizationBalance()
                .subtract(confirmBalanceDto.amount()));
        return saveAndMap(balance, "confirming an authorization");
    }

    @Override
    @Transactional
    public BalanceDto release(long balanceId, ChangedBalanceDto releaseBalanceDto) {
        balanceValidator.validateAmount(releaseBalanceDto.amount());
        Balance balance = balanceValidator.validateBalanceExisting(balanceId);
        log.debug("Balance {} has been found", balance);
        balanceValidator.validateEnoughAuthorizationBalance(balance, releaseBalanceDto.amount());
        balance.setAuthorizationBalance(balance
                .getAuthorizationBalance()
                .subtract(releaseBalanceDto.amount()));
        balance.setActualBalance(balance
                .getActualBalance()
                .add(releaseBalanceDto.amount()));
        return saveAndMap(balance, "releasing an authorization");
    }

    private BalanceDto saveAndMap(Balance balance, String operation) {
        try {
            Balance savedBalance = balanceRepository.save(balance);
            return balanceMapper.toBalanceDto(savedBalance);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            log.error("Optimistic lock error while {} balance: {}", operation, balance.getId());
            throw new InvalidBalanceOperationException(
                    String.format("Balance %d was modified by another process. "
                            + "Please try again.", balance.getId()));
        }
    }
}