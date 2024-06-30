package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceValidator balanceValidator;

    public BalanceDto getBalance(Long balanceId) {
        balanceValidator.checkIsNull(balanceId);
        return balanceMapper.toDto(balanceRepository.findById(balanceId).orElseThrow(() ->
                new EntityNotFoundException("The balance with id " + balanceId + " does not exist in the database")));
    }

    public BalanceDto createBalance(BalanceDto balanceDto) {
        balanceValidator.checkIsNull(balanceDto);

        Balance balance = balanceMapper.toEntity(balanceDto);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    public BalanceDto updateBalance(BalanceDto balanceDto) {
        balanceValidator.checkIsNull(balanceDto);
        balanceValidator.checkExistsBalanceInBd(balanceDto);

        Balance balance = balanceMapper.toEntity(balanceDto);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }
}
