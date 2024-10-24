package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.balance.BalanceDto;
import faang.school.accountservice.model.dto.balance.TransferResultDto;

import java.math.BigDecimal;

public interface BalanceService {

    BalanceDto getBalance(long balanceId);

    BalanceDto createBalance(BalanceDto balanceDto);

    BalanceDto increaseBalance(long balanceId, BigDecimal amount);

    BalanceDto decreaseBalance(long balanceId, BigDecimal amount);

    BalanceDto reserveBalance(long balanceId, BigDecimal amount);

    BalanceDto cancelReservation(long balanceId);

    BalanceDto releaseReservedBalance(long balanceId, BigDecimal amount);

    TransferResultDto transferBalance(long fromAccountId, long toAccountId, BigDecimal amount);
}
