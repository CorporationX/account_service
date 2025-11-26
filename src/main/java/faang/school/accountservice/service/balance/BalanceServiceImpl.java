package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.exception.AccountValidateException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final AccountServiceImpl accountService;


    @Override
    public BalanceResponseDto createBalance(long accountId) {

        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public BalanceResponseDto updateBalance(long accountId, BalanceUpdateDto balanceUpdateDto) {

        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public BalanceResponseDto getBalance(long balanceId) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow(
                ()-> new EntityNotFoundException("Balance not found %d".formatted(balanceId)));
        return balanceMapper.toDto(balance);
    }

    private void validateAccount(long accountId) {
        AccountResponseDto dto = accountService.getAccount(accountId);
        if (dto.status().equals(AccountStatusType.CLOSED)
                || dto.status().equals(AccountStatusType.FROZEN) ) {
            throw new AccountValidateException("Account status is invalid %s".formatted(dto.status()));
        }
    }
}