package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.repository.RequestJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceJpaRepository balanceRepository;
    private final BalanceMapper mapper;

    public void create(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balanceRepository.save(balance);
    }

    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }

    public BalanceDto getBalance(long accountId) {
        return mapper.toDto(balanceRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Not found account with id = " + accountId)));
    }
}
