package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceUpdateCommand;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.exception.*;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.repository.TransactionsRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static faang.school.accountservice.enums.TransactionType.RECEIVE;
import static faang.school.accountservice.enums.TransactionType.SEND;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final TransactionsRepository transactionsRepository;
    private final UserContext context;

    @Transactional(readOnly = true)
    public BalanceResponseDto getBalance(Long balanceId) {
        Balance balance = validateBalance(balanceId);
        validateOwner(balance.getAccount());
        log.info("Запрос на получение баланса для счета {} выполнен", balanceId);
        return new BalanceResponseDto(
                balance.getAvailableBalance(),
                balance.getCurrentBalance());
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    @Transactional
    public void addFunds(Long balanceId, BigDecimal amount, String comment) {
        Balance balance = validateBalance(balanceId);
        validateOwner(balance.getAccount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WrongAmountException("Депозит должен быть больше 0");
        }

        BalanceUpdateCommand command = BalanceUpdateCommand.builder()
                .availableBalanceDelta(amount)
                .currentBalanceDelta(amount)
                .build();

        updateBalance(balanceId, command);

        Transaction transaction = buildTransaction(balance, amount, TransactionType.DEPOSIT, comment);
        balance.getTransactions().add(transaction);
        log.info("Депозит на счет {} в размере {} успешно завершен", balanceId, amount);
        transactionsRepository.save(transaction);
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    @Transactional
    public void withdrawFunds(Long balanceId, BigDecimal amount, String comment) {
        Balance balance = validateBalance(balanceId);
        validateOwner(balance.getAccount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WrongAmountException("Сумма должна быть больше 0");
        }

        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new NotEnoughFundsException("Недостаточно средств для списания");
        }

        BalanceUpdateCommand command = BalanceUpdateCommand.builder()
                .availableBalanceDelta(amount.negate())
                .currentBalanceDelta(amount.negate())
                .build();

        updateBalance(balanceId, command);

        Transaction transaction = buildTransaction(balance, amount.negate(), TransactionType.WITHDRAW, comment);
        balance.getTransactions().add(transaction);
        log.info("Средства в размере {} успешно выведены со счета {}", amount, balanceId);
        transactionsRepository.save(transaction);
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    @Transactional
    public void sendPayment(Long senderId, Long receiverId, BigDecimal amount, String comment) {
        Balance sender = validateBalance(senderId);
        validateOwner(sender.getAccount());
        Balance receiver = validateBalance(receiverId);

        if (sender.getAvailableBalance().compareTo(amount) < 0) {
            throw new NotEnoughFundsException("Недостаточно средств для перевода");
        }
        if (senderId.equals(receiverId)) {
            throw new SelfPayException("Нельзя переводить себе");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WrongAmountException("Сумма должна быть больше 0");
        }

        updateBalance(senderId, new BalanceUpdateCommand(amount.negate(), amount.negate()));
        updateBalance(receiverId, new BalanceUpdateCommand(amount, amount));

        Transaction sendTx = buildTransaction(sender, amount, SEND, comment);
        Transaction receiveTx = buildTransaction(receiver, amount, RECEIVE, comment);

        transactionsRepository.saveAll(List.of(sendTx, receiveTx));
        sender.getTransactions().add(sendTx);
        receiver.getTransactions().add(receiveTx);

        log.info("Перевод {} от {} к {}", amount, senderId, receiverId);
    }

    private Transaction buildTransaction(Balance balance, BigDecimal amount,
                                         TransactionType type, String comment) {
        return Transaction.builder()
                .balance(balance)
                .amount(amount)
                .type(type)
                .comment(comment)
                .build();
    }

    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    @Transactional
    private Balance updateBalance(Long balanceId, BalanceUpdateCommand command) {
        Balance balance = validateBalance(balanceId);

        balance.setCurrentBalance(
                balance.getCurrentBalance().add(command.currentBalanceDelta()));

        balance.setAvailableBalance(
                balance.getAvailableBalance().add(command.availableBalanceDelta()));

        if (balance.getAvailableBalance().compareTo(balance.getCurrentBalance()) > 0) {
            throw new NotEnoughFundsException("Ошибка операции, доступный баланс больше текущего");
        }

        return balanceRepository.save(balance);
    }

    private Balance validateBalance(Long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> {
                    log.error("Баланс с таким айди не найден: {}", balanceId);
                    return new BalanceNotFoundException("Баланс не найден");
                });
    }

    private void validateOwner(Account account) {
        if (account == null || context.getUserId() == 0L) {
            throw new IllegalArgumentException("Account или UserContext не инициализированы");
        }
        if (account.getOwnerId() != (context.getUserId())) {
            throw new AccessException("Нет доступа к этому аккаунту");
        }
    }

}