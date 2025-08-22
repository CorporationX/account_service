package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountStatus;

/**
 * DTO для обновления статуса счета
 *
 * @param status обновленный статус
 * @author mrnght
 * @since 22.08.2025
 */
public record AccountUpdateDto(
        AccountStatus status
) {
}
