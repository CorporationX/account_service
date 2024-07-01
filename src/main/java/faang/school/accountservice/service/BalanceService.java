package faang.school.accountservice.service;

import faang.school.accountservice.jpa.BalanceJpaRepository;
import faang.school.accountservice.model.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceJpaRepository balanceJpaRepository;

    @Transactional
    public Balance create(Balance balance) {
        return balanceJpaRepository.save(balance);
    }
}
