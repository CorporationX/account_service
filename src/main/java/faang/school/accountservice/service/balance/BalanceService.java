package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.balance.AuthorizationBalance;
import faang.school.accountservice.entity.balance.AuthorizationBalanceType;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.balance.AuthorizationBalanceNotFoundException;
import faang.school.accountservice.exception.balance.BalanceNotFoundException;
import faang.school.accountservice.exception.balance.IllegalAuthorizationStatusException;
import faang.school.accountservice.exception.balance.NotEnoughAvailableFundsException;
import faang.school.accountservice.repository.balance.AuthorizationBalanceRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
                .orElseThrow(() -> getBalanceNotFound(balanceId));
    }

    @Transactional
    public Balance authorizeBalance(UUID balanceId, BigDecimal amount) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        BigDecimal available = balance.getBalance().subtract(balance.getAuthorizedBalance());
        if (amount.compareTo(available) > 0) {
            log.error("Not enough available funds: requested {}, available {}", amount, available);
            throw new NotEnoughAvailableFundsException(balanceId, amount, available);
        }

        AuthorizationBalance authorizationBalance = new AuthorizationBalance();
        authorizationBalance.setAmount(amount);
        authorizationBalance.setBalance(balance);
        authorizationBalance.setType(AuthorizationBalanceType.AUTHORIZED);
        // TODO: ttl из конфигурации
        authorizationBalance.setExpiresAt(LocalDateTime.now().plusDays(7));
        authorizationBalanceRepository.save(authorizationBalance);
        log.info("Authorization balance {} has been saved", authorizationBalance.getId());

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));

        log.info("Balance {} has been updated", balance);

        return balance;
    }

    @Transactional
    public Balance clearAuthorizationBalance(UUID authBalanceId) {
        AuthorizationBalance auth = authorizationBalanceRepository.findById(authBalanceId)
                .orElseThrow(() -> getAuthBalanceNotFound(authBalanceId));

        if (!Objects.equals(auth.getType(), AuthorizationBalanceType.AUTHORIZED)) {
            throw new IllegalAuthorizationStatusException(authBalanceId, auth.getType());
        }

        Balance balance = getBalanceByIdForUpdate(auth.getBalance().getId());

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(auth.getAmount()));
        balance.setBalance(balance.getBalance().subtract(auth.getAmount()));

        auth.setType(AuthorizationBalanceType.CLEARED);

        log.info("Authorization balance cleared {}", auth);
        log.info("Balance {} has been updated", balance);

        return balance;
    }

    @Transactional
    public Balance cancelAuthorizationBalance(UUID authBalanceId) {
        AuthorizationBalance auth = authorizationBalanceRepository.findById(authBalanceId)
                .orElseThrow(() -> new AuthorizationBalanceNotFoundException(authBalanceId));

        if (!Objects.equals(auth.getType(), AuthorizationBalanceType.AUTHORIZED)) {
            throw new IllegalAuthorizationStatusException(authBalanceId, auth.getType());
        }

        Balance balance = getBalanceByIdForUpdate(auth.getBalance().getId());

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(auth.getAmount()));

        auth.setType(AuthorizationBalanceType.CANCELED);

        log.info("Authorization balance canceled {}", auth);
        log.info("Balance {} has been updated", balance);

        return balance;
    }

    @Transactional
    // TODO: название для метода
    public Balance expiredAuthorizationBalance(AuthorizationBalance auth) {

        if (!Objects.equals(auth.getType(), AuthorizationBalanceType.AUTHORIZED)) {
            throw new IllegalAuthorizationStatusException(auth.getId(), auth.getType());
        }

        Balance balance = getBalanceByIdForUpdate(auth.getBalance().getId());

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(auth.getAmount()));

        auth.setType(AuthorizationBalanceType.CANCELED);

        log.info("Authorization balance canceled {}", auth);
        log.info("Balance {} has been updated", balance);

        return balance;
    }

    @Transactional(readOnly = true)
    public List<AuthorizationBalance> getExpiredAuthorizationBalance() {
        return authorizationBalanceRepository.getExpiredAuthorizationBalance();
    }

    public Balance getBalanceByIdForUpdate(UUID balanceId) {
        return balanceRepository.findByIdForUpdate(balanceId)
                .orElseThrow(() -> getBalanceNotFound(balanceId));
    }

    private BalanceNotFoundException getBalanceNotFound(UUID balanceId) {
        log.error("Balance with id {} not found", balanceId);
        return new BalanceNotFoundException(balanceId);
    }

    private AuthorizationBalanceNotFoundException getAuthBalanceNotFound(UUID authBalanceId) {
        log.error("Authorization balance with id {} not found", authBalanceId);
        return new AuthorizationBalanceNotFoundException(authBalanceId);
    }
}
