package faang.school.accountservice.service.balance;

import java.math.BigDecimal;

public interface BalanceService {

    /**
     * Получает баланс счета без блокировки.
     *
     * @param accountId идентификатор счета
     * @return баланс счета
     * @throws IllegalStateException если баланс не найден для указанного счета
     */
    BigDecimal getBalance(Long accountId);

    /**
     * Получает баланс счета с пессимистической блокировкой.
     * Используется для предотвращения race condition при параллельных операциях.
     *
     * @param accountId идентификатор счета
     * @return баланс счета
     * @throws IllegalStateException если баланс не найден для указанного счета
     */
    BigDecimal getBalanceWithLock(Long accountId);
}

