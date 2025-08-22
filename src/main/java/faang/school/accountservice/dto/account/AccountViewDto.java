package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.entity.account.AccountType;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.enums.Currency;

/**
 * DTO для отображения счета пользователю
 *
 * @param accountNumber номер счета
 * @param type тип владельца счета: пользователь или проект
 * @param ownerId идентификатор владельца
 * @param accountType тип счета
 * @param currency тип валюты
 * @param status статус счета
 */
public record AccountViewDto(
        String accountNumber,
        OwnerType type,
        Long ownerId,
        AccountType accountType,
        Currency currency,
        AccountStatus status
) {
}
