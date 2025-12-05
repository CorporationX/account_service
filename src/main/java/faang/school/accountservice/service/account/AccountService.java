package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.OpenAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    /**
     * Получить счёт по ID.
     *
     * @param id идентификатор счёта
     * @return DTO счёта
     * @throws AccountNotFoundException если счёт не найден
     */
    AccountResponseDto get(@NotNull Long id);

    /**
     * Получить счёт по номеру.
     *
     * @param number номер счёта
     * @return DTO счёта
     * @throws AccountNotFoundException если счёт не найден
     */
    AccountResponseDto getByNumber(@NotBlank String number);

    /**
     * Получить все счета владельца с пагинацией.
     *
     * @param ownerId   ID владельца
     * @param ownerType тип владельца
     * @param pageable  параметры пагинации
     * @return страница счетов
     */
    Page<AccountResponseDto> getByOwner(
            @NotNull Long ownerId,
            @NotNull OwnerType ownerType,
            @NotNull Pageable pageable);

    /**
     * Получить активные счета владельца с пагинацией.
     *
     * @param ownerId   ID владельца
     * @param ownerType тип владельца
     * @param pageable  параметры пагинации
     * @return страница активных счетов
     */
    Page<AccountResponseDto> getActiveByOwner(
            @NotNull Long ownerId,
            @NotNull OwnerType ownerType,
            @NotNull Pageable pageable);

    /**
     * Получить активные счета владельца в определённой валюте с пагинацией.
     *
     * @param ownerId   ID владельца
     * @param ownerType тип владельца
     * @param currency  валюта
     * @param pageable  параметры пагинации
     * @return страница активных счетов в указанной валюте
     */
    Page<AccountResponseDto> getActiveByOwnerAndCurrency(
            @NotNull Long ownerId,
            @NotNull OwnerType ownerType,
            @NotNull Currency currency,
            @NotNull Pageable pageable);

    /**
     * Открыть новый счёт.
     *
     * @param dto данные для открытия счёта
     * @return DTO созданного счёта
     * @throws AccountOperationException если превышен лимит счетов или номер уже существует
     */
    AccountResponseDto open(@Valid OpenAccountDto dto);

    /**
     * Заблокировать счёт.
     *
     * @param id идентификатор счёта
     * @return DTO заблокированного счёта
     * @throws AccountNotFoundException   если счёт не найден
     * @throws AccountOperationException  если счёт нельзя заблокировать
     */
    AccountResponseDto block(@NotNull Long id);

    /**
     * Разблокировать счёт.
     *
     * @param id идентификатор счёта
     * @return DTO разблокированного счёта
     * @throws AccountNotFoundException   если счёт не найден
     * @throws AccountOperationException  если счёт нельзя разблокировать
     */
    AccountResponseDto unblock(@NotNull Long id);

    /**
     * Закрыть счёт.
     *
     * @param id идентификатор счёта
     * @return DTO закрытого счёта
     * @throws AccountNotFoundException   если счёт не найден
     * @throws AccountOperationException  если счёт нельзя закрыть (ненулевой баланс)
     */
    AccountResponseDto close(@NotNull Long id);
}
