package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Async
    @Transactional
    public BalanceDto saveBalance(BalanceDto balanceDto) {
        if (balanceDto == null) {
            throw new NullPointerException("Balance is null");
        }
        Balance balance = balanceMapper.toBalance(balanceDto);
        balance.setAccount(accountRepository.findById(balanceDto.getAccountId()).orElseThrow());

        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Async
    @Transactional
    public void updateBalance(long accountId, BigInteger authBalance) {
        Balance balance = balanceRepository.findByAccountId(accountId);
        writeOff(balance, authBalance);
        balanceRepository.updateByAccountId(accountId, balance.getCurrBalance());
    }

    public void writeOff(Balance balance, BigInteger authBalance) {
        if (balance.getCurrBalance().compareTo(authBalance) < 0) {
            throw new IllegalArgumentException("Current balance is less than auth balance");
        }
        BigInteger newCurrBalance = balance.getCurrBalance().subtract(authBalance);
        balance.setCurrBalance(newCurrBalance);
    }

}
