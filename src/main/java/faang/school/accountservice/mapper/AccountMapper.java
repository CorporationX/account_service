package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;

public class AccountMapper {

    public static AccountDto toDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getAccountNumber(),
                account.getUserId(),
                account.getType(),
                account.getCurrency(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.getClosedAt(),
                account.getFrozenAt(),
                account.getBlockedAt(),
                account.getBalance(),
                account.getDescription(),
                account.getCloseReason(),
                account.getFrozenReason(),
                account.getBlockReason()
        );
    }

    public static Account toEntity(AccountCreateDto dto) {
        return Account.builder()
                .type(dto.type())
                .currency(dto.currency())
                .description(dto.description())
                .build();
    }
}
