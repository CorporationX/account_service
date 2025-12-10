package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Интерфейс сервиса для управления балансами платёжных аккаунтов.
 */
public interface BalanceService {

    /**
     * Возвращает текущий баланс по идентификатору аккаунта.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @return баланс аккаунта
     */
    Balance getBalance(@NonNull UUID accountId);

    /**
     * Авторизует (резервирует) указанную сумму на аккаунте.
     *
     * @param accountId         идентификатор аккаунта, не {@code null}
     * @param amountToAuthorize сумма для авторизации, не {@code null}
     * @return обновлённый баланс
     */
    Balance authorize(@NonNull UUID accountId, @NonNull BigDecimal amountToAuthorize);

    /**
     * Пополняет фактический баланс аккаунта на указанную сумму.
     *
     * @param accountId    идентификатор аккаунта, не {@code null}
     * @param actualAmount сумма пополнения, не {@code null}
     * @return обновлённый баланс
     */
    Balance topUpActualBalance(@NonNull UUID accountId, @NonNull BigDecimal actualAmount);

    /**
     * Создаёт новый баланс для указанного аккаунта с начальным фактическим значением.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @param amount    начальный фактический баланс, не {@code null}
     * @return созданный баланс
     */
    Balance create(@NonNull UUID accountId, @NonNull BigDecimal amount);


    Balance update(@NonNull UUID accountId, @NonNull UpdateBalanceDto updateBalanceDto);
}