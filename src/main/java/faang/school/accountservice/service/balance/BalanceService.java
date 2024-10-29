package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDtoWhenUpdate;
import faang.school.accountservice.dto.balance.ReturnedBalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public void create(Account account) {
        log.info("Create method started for accountId: {}", account.getId());

        Balance balance = Balance.builder()
                .actualBalance(0)
                .account(account)
                .build();

        balance = balanceRepository.save(balance);
        log.debug("Balance created: {}", balance);
    }

    @Transactional
    public ReturnedBalanceDto update(long accountId, BalanceDtoWhenUpdate balanceDtoWhenUpdate) {
        log.info("Update method started for accountId: {}", accountId);
        Balance balance = balanceMapper.toBalance(balanceDtoWhenUpdate);

        Account account = findAccountById(accountId);

        balance.setAccount(account);
        balance.setCreatedAt(account.getBalance().getCreatedAt());
        log.debug("Balance updated: {}", balance);

        log.debug("balance has been updated, balance: {}", balance);
        ReturnedBalanceDto result = balanceMapper.toReturnedBalanceDto(balance);
        result.setAccountId(accountId);
        log.debug("Update method completed successfully, result: {}", result);
        return result;
    }

    private Account findAccountById(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account not found with id: %d", accountId))
        );
    }
}
