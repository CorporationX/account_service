package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceDto createBalance(Account account, long authorisationBalance, long actualBalance) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(actualBalance);
        balance.setAuthorisationBalance(authorisationBalance);
        balance.setCreatedAt(LocalDateTime.now());
        balance.setUpdatedAt(LocalDateTime.now());
        balance.setVersion(1L);

        Balance savedBalance = balanceRepository.save(balance);

        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto updateBalance(Long balanceId, long authorisationBalance, long actualBalance) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found with id" + balanceId));

        balance.setAuthorisationBalance(authorisationBalance);
        balance.setActualBalance(actualBalance);
        balance.setUpdatedAt(LocalDateTime.now());
        balance.setVersion(balance.getVersion() + 1);

        Balance savedBalance = balanceRepository.save(balance);

        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto getBalance(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found with id: " + balanceId));
        return balanceMapper.toDto(balance);
    }
}

