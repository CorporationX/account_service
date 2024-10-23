package faang.school.accountservice.service.impl.balance;

import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.dto.BalanceDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.validator.BalanceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceValidator balanceValidator;

    @Transactional(readOnly = true)
    public BalanceDto getBalance(long balanceId) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(() ->
                new EntityNotFoundException("The balance with ID %d doesn't exist!".formatted(balanceId)));
        return balanceMapper.toDto(balance);
    }

    public BalanceDto createBalance(BalanceDto balanceDto) {
        return saveBalance(balanceDto);
    }

    public BalanceDto updateBalance(BalanceDto balanceDto) {
        balanceValidator.balanceExists(balanceDto);
        return saveBalance(balanceDto);
    }

    private BalanceDto saveBalance(BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }
}
