package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.exception.AccountValidateException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
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
    private final AccountRepository accountRepository;


    @Override
    public BalanceResponseDto createBalance(long accountId) {
        validateAccount(accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found" + accountId));
        if (account.getBalance() != null) {
            throw new ForbiddenException("There is already a balance on the account");
        }
        Balance newBalance = Balance.builder()
                .account(account)
                .build();
        return balanceMapper.toDto(balanceRepository.save(newBalance));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public BalanceResponseDto updateBalance(long accountId, BalanceUpdateDto balanceUpdateDto) {
        validateAccount(accountId);
        Balance balance = balanceRepository.findByAccountId(accountId);
        long authorizationAmount =
                Math.addExact(balance.getAuthorizationAmount(), balanceUpdateDto.authorizationAmount());
        balance.setAuthorizationAmount(authorizationAmount);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public BalanceResponseDto getBalance(long accountId) {
        Balance balance = balanceRepository.findByAccountId(accountId);
        return balanceMapper.toDto(balance);
    }

    private void validateAccount(long accountId) {
        AccountDto dto = accountService.getAccount(accountId);
        if (dto.status().equals(AccountStatusType.CLOSED)
                || dto.status().equals(AccountStatusType.FROZEN)) {
            throw new AccountValidateException("Account status is invalid %s".formatted(dto.status()));
        }
    }
}