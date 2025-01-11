package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceDto createBalance(Account account) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(0L);
        balance.setAuthorisationBalance(0L);
        balance.setVersion(1L);

        Balance savedBalance = balanceRepository.save(balance);

        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto updateBalance(Long balanceId, long authorisationBalance, long actualBalance) {
        try {
            Balance balance = balanceRepository.findById(balanceId)
                    .orElseThrow(() -> new IllegalArgumentException("Balance not found with id" + balanceId));

            balance.setAuthorisationBalance(authorisationBalance);
            balance.setActualBalance(actualBalance);

            Balance savedBalance = balanceRepository.save(balance);

            return balanceMapper.toDto(savedBalance);
        } catch (OptimisticLockException e) {
            throw new IllegalArgumentException("The balance has been updated by another transaction. Please try again.");
        }
    }

    @Transactional
    public BalanceDto getBalance(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found with id: " + balanceId));
        return balanceMapper.toDto(balance);
    }
}

