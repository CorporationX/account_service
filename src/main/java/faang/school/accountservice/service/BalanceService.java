package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.repository.BalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceJpaRepository repository;

    public void create(BalanceDto balanceDto) {

    }

    public void update(BalanceDto balanceDto) {

    }

    public double getBalance(long accountId) {

    }
}
