package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getOwnerId(),
            account.getOwnerType(),
            account.getAccountType(),
            account.getCurrency(),
            account.getStatus(),
            account.getBalance(),
            account.getCreatedAt(),
            account.getUpdatedAt(),
            account.getClosedAt(),
            account.getVersion()
        );
    }
}