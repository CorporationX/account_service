package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountType;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.enums.Currency;

/**
 * DTO для открытия нового счета
 *
 * @param type тип владельца: пользователь или целый проект
 * @param ownerId уникальный идентификатор владельца
 * @param accountType тип счета
 * @param currency тип валюты
 * @author mrnght
 * @since 22.08.2025
 */
public record AccountCreateDto(
        OwnerType type,
        Long ownerId,
        AccountType accountType,
        Currency currency
) {
}
