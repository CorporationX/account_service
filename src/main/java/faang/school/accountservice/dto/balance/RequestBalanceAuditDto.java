package faang.school.accountservice.dto.balance;

public record RequestBalanceAuditDto(
        Long accountId,
        Long operationId
) {
}
