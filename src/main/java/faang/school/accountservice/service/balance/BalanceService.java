package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
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
    public void update(Balance balance) {
        log.info("Update method started for accountId: {}", balance.getId());
        balance = balanceRepository.save(balance);
        log.debug("balance has been updated, balance: {}", balance);
    }

    public Balance getBalance(long balanceId) {
        return balanceRepository.findById(balanceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account not found with id: %d", balanceId))
        );
    }
}
