package faang.school.accountservice.dto.balance;

public record BalanceDto(
        Long id,
        Long accountId,
        Long authorizationBalance,
        Long actualBalance
) {
}
