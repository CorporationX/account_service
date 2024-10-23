package faang.school.accountservice.service;

import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    @Transactional
    public Balance multiplyCurrentBalance(Long balanceId, Double value) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow();
        BigDecimal currentBalance = balance.getCurrentBalance();

        BigDecimal multiplier = BigDecimal.valueOf(value);
        BigDecimal newCurrentBalance = currentBalance.add(currentBalance.multiply(multiplier));

        balance.setCurrentBalance(newCurrentBalance);
        return balanceRepository.save(balance);
    }
}
