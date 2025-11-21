package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;

public class AccountMapper {

    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .userId(account.getUserId())
                .type(account.getType())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .balance(account.getBalance())
                .description(account.getDescription())
                .statusChangeReason(account.getStatusChangeReason())
                .build();
    }

    public static Account toEntity(AccountCreateDto dto) {
        return Account.builder()
                .type(dto.type())
                .currency(dto.currency())
                .description(dto.description())
                .build();
    }
}
