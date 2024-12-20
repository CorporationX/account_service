package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import jakarta.validation.Valid;

public interface BalanceService {

  BalanceDto create(@Valid Long userId, @Valid BalanceDto balanceDto);

  BalanceDto update(@Valid Long userId, @Valid BalanceDto balanceDto);

  BalanceDto getById(@Valid Long userId, Long id);

}
