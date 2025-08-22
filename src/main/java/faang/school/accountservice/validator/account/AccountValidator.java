package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.account.AccountOwnershipException;
import faang.school.accountservice.exception.account.IllegalStatusTransitionException;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

    public void validateCreate(@NonNull CreateAccountDto createAccountDto) {
        checkOwnership(createAccountDto);
    }

    public void validateStatusTransition(Account account, AccountStatus newStatus) {
        if (account.getStatus() == newStatus) {
            throw new IllegalStatusTransitionException("Account already in this status");
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStatusTransitionException("Account closed. Further updates not allowed");
        }

    }

    private void checkOwnership(CreateAccountDto createAccountDto) {
        if(createAccountDto.projectId() == null && createAccountDto.userId() == null){
            throw new AccountOwnershipException("Account must have either a user or a project id ");
        }

        if(createAccountDto.projectId() != null && createAccountDto.userId() != null){
            throw new AccountOwnershipException("Account cannot belong to user and project at the same time");
        }
    }
}
