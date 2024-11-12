package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.BalanceException;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final TransactionService transactionService;

    @Transactional
    public void create(Account account) {
        log.info("method create started for accountId: {}", account.getId());

        Balance balance = Balance.builder()
                .actualBalance(BigDecimal.ZERO)
                .account(account)
                .build();
        log.debug("balance is created, balance: {}", balance);

        balance = balanceRepository.save(balance);
        log.info("method create finished,  balance created: {}", balance);
    }

    @Transactional
    public BalanceDto update(long accountId, TransactionDto dto) {

        log.info("method update started for accountId: {}", accountId);

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account not found with id: %d", accountId))
        );

        if (account.getCurrency() != dto.getCurrency()) {
            throw new BalanceException(String.format("incorrect currency, %s is required, perform the conversion",
                    account.getCurrency()));
        }

        Balance newBalance = transactionService.processTransaction(dto, account);
        log.debug("a new balance was received, new balance {}", newBalance);

        newBalance = balanceRepository.save(newBalance);
        BalanceDto balanceDto = balanceMapper.toBalanceDto(newBalance);
        log.info("method update finished, balance has been updated, balance: {}", balanceDto);

        return balanceDto;
    }

    public BalanceDto getBalance(long accountId) {
        log.info("method getBalance started for account: {}", accountId);

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account not found with id: %d", accountId))
        );
        log.debug("the balance was read from the database, account: {}", account);

        Balance balance = account.getBalance();
        BalanceDto balanceDto = balanceMapper.toBalanceDto(balance);
        balanceDto.setAccountId(accountId);
        log.info("method getBalance finished, balanceDto {}", balanceDto);
        return balanceDto;
    }
}
