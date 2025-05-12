package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {
    private static final String ACCOUNT_NOT_FOUND_MSG = "Account with id %d not exists";
    private static final String BALANCE_NOT_FOUND_MSG = "Balance with id %d not exists";

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public ResponseBalanceDto create(long accountId) {
        return balanceMapper.toDto(balanceRepository.save(Balance.builder()
                .account(accountRepository.findById(accountId)
                        .orElseThrow(() -> new EntityNotFoundException(format(ACCOUNT_NOT_FOUND_MSG, accountId))))
                .actualBalance(0)
                .authorizationBalance(0)
                .build()));
    }

    @Transactional
    public ResponseBalanceDto find(long balanceId) {
        return balanceMapper.toDto(getBalanceById(balanceId));
    }

    @Transactional
    public ResponseBalanceDto update(UpdateBalanceDto balanceDto) {
        Balance balance = getBalanceById(balanceDto.getId());

        if (balanceDto.getAuthorizationBalance() != null) {
            balance.setAuthorizationBalance(balanceDto.getAuthorizationBalance());
        }

        if (balanceDto.getActualBalance() != null) {
            balance.setActualBalance(balanceDto.getActualBalance());
        }
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public void delete(long balanceId) {
        balanceRepository.deleteById(balanceId);
    }

    private Balance getBalanceById(long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException(format(BALANCE_NOT_FOUND_MSG, balanceId)));
    }
}
