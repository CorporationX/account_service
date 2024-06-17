package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
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
    private final BalanceMapper balanceMapper;

    @Transactional
    public void createBalance(Account account) {
        Balance balance = initializeBalance(account);
        balanceRepository.save(balance);
        log.info("Created balance for account: {}", account.getId());
    }

    @Transactional
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance balance = balanceRepository.save(balanceMapper.toEntity(balanceDto));
        return balanceMapper.toDto(balance);
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalance(long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Balance: %d not found", balanceId)));
        return balanceMapper.toDto(balance);
    }

    private Balance initializeBalance(Account account) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(BigDecimal.ZERO);
        balance.setAuthorizationBalance(BigDecimal.ZERO);
        return balance;
    }
}