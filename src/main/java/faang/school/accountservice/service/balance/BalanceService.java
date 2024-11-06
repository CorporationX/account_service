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
        String methodName = getMethodName();
        log.info("method {} started for accountId: {}", methodName, account.getId());

        Balance balance = Balance.builder()
                .actualBalance(BigDecimal.ZERO)
                .account(account)
                .build();

        balance = balanceRepository.save(balance);
        log.info("method {} finished,  balance created: {}", methodName, balance);
    }

    @Transactional
    public BalanceDto update(TransactionDto dto) {

        long accountId = dto.getAccount();
        String methodName = getMethodName();
        log.info("method {} started for accountId: {}", methodName, dto.getAccount());

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
        log.info("method {} finished, balance has been updated, balance: {}", methodName, balanceDto);

        return balanceDto;
    }

    public BalanceDto getBalance(long accountId) {
        String methodName = getMethodName();
        log.info("method {} started for account: {}", methodName, accountId);

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account not found with id: %d", accountId))
        );
        log.debug("the balance was read from the database, account: {}", account);

        Balance balance = account.getBalance();
        BalanceDto balanceDto = balanceMapper.toBalanceDto(balance);
        balanceDto.setAccountId(balance.getAccount().getId());
        log.info("method {} finished, balanceDto {}", methodName, balanceDto);
        return balanceDto;
    }

    private String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
