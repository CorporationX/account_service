package faang.school.accountservice.service.impl.balance;

import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.dto.BalanceDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional(readOnly = true)
    public BalanceDto getBalance(long balanceId) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(() ->
                new EntityNotFoundException("The balance with ID %d doesn't exist!".formatted(balanceId)));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto createBalance(BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Transactional
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(balanceDto.id()).orElseThrow(() ->
                new EntityNotFoundException("The balance with ID %d doesn't exist!".formatted(balanceDto.id())));
        balance.setUpdatedAt(LocalDateTime.now());
        balance.setCurrentActualBalance(balanceDto.currentActualBalance());
        balance.setCurrentAuthorizationBalance(balanceDto.currentAuthorizationBalance());
        return balanceMapper.toDto(balanceRepository.save(balance));
    }
}
