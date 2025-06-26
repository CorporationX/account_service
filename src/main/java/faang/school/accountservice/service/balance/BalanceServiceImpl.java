package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceDto create(BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        Balance savedBalance = balanceRepository.save(balance);
        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto update(Long id, BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Balance not found"));
        balanceMapper.update(balance, balanceDto);
        Balance updatedBalance = balanceRepository.save(balance);
        return balanceMapper.toDto(updatedBalance);
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalance(Long id) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Balance not found"));
        return balanceMapper.toDto(balance);
    }
}
