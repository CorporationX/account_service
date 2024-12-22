package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.model.balance.Balance;
import jakarta.validation.Valid;

public interface BalanceService {

    BalanceDto create(@Valid Long userId, @Valid BalanceCreateDto balanceCreateDto);

    BalanceDto update(@Valid Long userId, @Valid PaymentDto paymentDto);

    Balance findBalanceById(Long id);

    BalanceDto getBalanceById(@Valid Long userId, Long id);
}