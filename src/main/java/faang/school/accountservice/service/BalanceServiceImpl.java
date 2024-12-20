package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

  private final BalanceRepository balanceRepository;
  private final BalanceMapper balanceMapper;

  @Transactional
  @Override
  public BalanceDto create(Long userId, BalanceDto balanceDto) {
    Balance balance = balanceMapper.toEntity(balanceDto);
    return balanceMapper.toDto(balanceRepository.save(balance));
  }

  @Override
  public BalanceDto update(Long userId, BalanceDto balanceDto) {
    return null;
  }

  @Override
  public BalanceDto getById(Long userId, Long id) {
    return null;
  }
}
