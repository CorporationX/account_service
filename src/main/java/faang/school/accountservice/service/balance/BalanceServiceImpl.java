package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ExclusionOfFundsException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ValidateAccount validateAccount;


    @Transactional
    @Override
    public BalanceResponseDto createBalance(long accountId) {
        validateAccount.validateAccount(accountService.getAccount(accountId));
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

    @Transactional
    @Override
    public BalanceResponseDto updateBalance(long accountId, BalanceUpdateDto balanceUpdateDto) {
        validateAccount.validateAccount(accountService.getAccount(accountId));
        Balance balance = getBalanceByAccountIdOrThrow(accountId);
        long authorizationAmount =
                Math.addExact(balance.getAuthorizationAmount(), balanceUpdateDto.authorizationAmount());
        if (authorizationAmount <= 0) {
            throw new ExclusionOfFundsException("Insufficient funds on the balance");
        }
        balance.setAuthorizationAmount(authorizationAmount);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Transactional(readOnly = true)
    @Override
    public BalanceResponseDto getBalance(long accountId) {
        Balance balance = getBalanceByAccountIdOrThrow(accountId);
        return balanceMapper.toDto(balance);
    }

    private Balance getBalanceByAccountIdOrThrow(long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not fount"));
    }
}