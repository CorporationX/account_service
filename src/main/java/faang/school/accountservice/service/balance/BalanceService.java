package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional(readOnly = true)
    public BalanceDto getBalance(Long balanceId) {
        Balance balance = balanceRepository
                .findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public Balance createBalance(Account account) {
        Balance balance = Balance.builder()
                .account(account)
                .authorizationBalance(BigDecimal.ZERO)
                .actualBalance(BigDecimal.ZERO)
                .build();

        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance updateBalance(BalanceDto balanceDto) {
        return balanceRepository.save(balanceMapper.toEntity(balanceDto));
    }
}
