package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
  private final BalanceRepository balanceRepository;

  @Override
  public BalanceDto create(Long userId, BalanceDto balanceDto) {
    return null;
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
