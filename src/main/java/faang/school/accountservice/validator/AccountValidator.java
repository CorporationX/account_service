package faang.school.accountservice.validator;


import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.user.UserContext;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import feign.FeignException;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final AccountRepository accountRepository;
    private final UserContext userContext;

    public void validateCreate(Account account) {
        validateNumberIsUnique(account);
        validateUserOrProjectExist(account);
        validateAccountIdForUserIsCorrect(account);
        validateUserIsProjectMember(account);
    }

    public void validateBlock(Account account) {
        validateStatus(account, AccountStatus.ACTIVE);
    }

    public void validateUnblock(Account account) {
        validateStatus(account, AccountStatus.FROZEN);
    }

    public void validateClose(Account account) {
        validateStatusForClose(account);
    }

    private void validateNumberIsUnique(Account account) {
        boolean numberIsUnique = accountRepository.checkNumberIsUnique(account.getNumber());
        if (!numberIsUnique) {
            throw new DataValidationException(String.format("Number is not unique %s", account.getNumber()));
        }
    }

    private void validateAccountIdForUserIsCorrect(Account account) {

        if (OwnerType.USER.equals(account.getOwner().getOwnerType())) {
            long userId = userContext.getUserId();
            if (userId != account.getOwner().getCustodianId()) {
                throw new DataValidationException(
                        "UserContext and accountId is different, only author could create new account");
            }
        }
    }

    private void validateUserIsProjectMember(Account account) {

        if (OwnerType.PROJECT.equals(account.getOwner().getOwnerType())) {
            boolean isOwner = projectServiceClient.checkProjectOwner(
                    account.getOwner().getCustodianId(),
                    userContext.getUserId());

            if (!isOwner) {
                throw new DataValidationException(
                        String.format("Only project owner could create account for project %d",
                                account.getOwner().getCustodianId()));
            }
        }
    }

    @Retryable(retryFor = ReadTimeoutException.class, backoff = @Backoff(delay = 2000))
    private void validateUserOrProjectExist(Account account) {
        try {
            if (OwnerType.USER.equals(account.getOwner().getOwnerType())) {
                userServiceClient.getUser(account.getOwner().getCustodianId());
            } else if (OwnerType.PROJECT.equals(account.getOwner().getOwnerType())) {
                projectServiceClient.getProject(account.getOwner().getCustodianId());
            }
        } catch (FeignException ex) {
            throw new NotFoundException(String.format("%s with id %d not found",
                    account.getOwner().getOwnerType(), account.getOwner().getCustodianId()));
        }
    }

    private void validateStatus(Account account, AccountStatus status) {
        if (!status.equals(account.getAccountStatus())) {
            throw new DataValidationException(
                    String.format("Status should be %s, but account: %d is %s",
                            status,
                            account.getId(),
                            account.getAccountStatus()));
        }
    }

    private void validateStatusForClose(Account account) {
        if (AccountStatus.CLOSED.equals(account.getAccountStatus())) {
            throw new DataValidationException(String.format("Account: %d already closed", account.getId()));
        }
    }
}