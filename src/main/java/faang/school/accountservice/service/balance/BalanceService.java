package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.AuthorizationBalanceClearType;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.AuthorizationBalance;
import faang.school.accountservice.entity.balance.AuthorizationBalanceType;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.balance.BalanceNotFoundException;
import faang.school.accountservice.repository.balance.AuthorizationBalanceRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private BalanceRepository balanceRepository;
    private AuthorizationBalanceRepository authorizationBalanceRepository;

    @Transactional(readOnly = true)
    public Balance getBalanceById(UUID balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> {
                    log.error("Balance with id {} not found", balanceId);
                    return new BalanceNotFoundException(balanceId);
                });
    }

    @Transactional
    public Balance authorizeBalance(UUID balanceId, BigDecimal amount) {
        // TODO: пессимистик лок
        Balance balance = getBalanceById(balanceId);

        // TODO: вичитать BigDecimal
        if (amount >= balance.getBalance() - balance.getAuthorizedBalance()) {
            // TODO: другое исключение
            throw new RuntimeException();
        }

        AuthorizationBalance authorizationBalance = new AuthorizationBalance();
        authorizationBalance.setAmount(amount);
        authorizationBalance.setBalance(balance);
        authorizationBalance.setType(AuthorizationBalanceType.AUTHORIZED);
        authorizationBalanceRepository.save(authorizationBalance);
        log.info("AuthorizationBalance {} has been saved", authorizationBalance);

        // TODO: правильный метод для BigDecimal
        balance.setAuthorizedBalance(balance.getBalance().plus(authorizationBalance.getAmount()));
        log.info("Balance {} has been updated", balance);

        return balance;
    }

    @Transactional
    public Balance clearAuthorizationBalance(UUID authBalanceId) {
        AuthorizationBalance authorizationBalance = authorizationBalanceRepository.findById(authBalanceId)
                .orElseThrow();

        // TODO: проверить что авторизованный баланс AUTHORIZED

        // TODO: пессимистик лок
        Balance balance = authorizationBalance.getBalance();

        balance.setAuthorizedBalance(balance.getAuthorizedBalance() - authorizationBalance.getAmount());
        balance.setBalance(balance.getBalance() - authorizationBalance.getAmount());

        authorizationBalance.setType(AuthorizationBalanceType.CLEARED);

        return balance;
    }

    @Transactional
    public Balance cancelAuthorizationBalance(UUID authBalanceId) {
        AuthorizationBalance authorizationBalance = authorizationBalanceRepository.findById(authBalanceId)
                .orElseThrow();

        // TODO: проверить что авторизованный баланс AUTHORIZED

        // TODO: пессимистик лок
        Balance balance = authorizationBalance.getBalance();

        balance.setAuthorizedBalance(balance.getAuthorizedBalance() - authorizationBalance.getAmount());

        authorizationBalance.setType(AuthorizationBalanceType.CANCELED);

        return balance;
    }
}
