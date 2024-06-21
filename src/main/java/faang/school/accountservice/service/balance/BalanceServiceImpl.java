package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Override
    @Transactional
    public BalanceDto createBalance(long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Account with id %d not found", accountId)));

        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(new BigDecimal(0));
        balance.setAuthorizationBalance(new BigDecimal(0));

        balanceRepository.save(balance);
        log.info("Created balance for account: {}", account.getId());
        return balanceMapper.toDto(balance);
    }

    @Override
    @Transactional
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(balanceDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Balance not found %d", balanceDto.getId())));
        balanceRepository.save(balanceMapper.toEntity(balanceDto));
        return balanceMapper.toDto(balance);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceDto getBalance(long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Balance: %d not found", balanceId)));
        return balanceMapper.toDto(balance);
    }

    @Override
    @Transactional()
    public void deleteBalance(long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Balance: %d not found", balanceId)));
        balanceRepository.delete(balance);
    }
}