package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceDto createBalance(BalanceDto balanceDto) {
        Balance savedBalance = balanceRepository.save(balanceMapper.toEntity(balanceDto));
        return balanceMapper.toDto(savedBalance);
    }


    @Transactional
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance savedBalance = balanceRepository.save(balanceMapper.toEntity(balanceDto));
        return balanceMapper.toDto(savedBalance);
    }


    public BalanceDto getBalance(long balanceId){
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(EntityNotFoundException::new);
        return balanceMapper.toDto(balance);
    }
}
