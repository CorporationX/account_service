package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.ProjectDto;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.ErrorType;
import faang.school.accountservice.enums.ProjectStatus;
import faang.school.accountservice.exception.EntityAlreadyBlockedException;
import faang.school.accountservice.exception.EntityAlreadyClosedException;
import faang.school.accountservice.exception.EntityCancelledException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.MoreOneOwnerException;
import faang.school.accountservice.exception.NotResourceOwnerException;
import faang.school.accountservice.exception.OwnerIdNotPresentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidator {
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateDto(CreateAccountDto accountDto) {
        log.info("Start account validation: {}", accountDto);
        validateOwnerIds(accountDto.userId(), accountDto.projectId());

        if (accountDto.userId() != null) {
            validateUser(accountDto.userId());
        } else {
            validateProject(accountDto.projectId());
        }

        log.info("Account is valid: {}", accountDto);
    }

    public void validateOwnerIds(Long userId, Long projectId) {
        log.debug("Start owner ids validation with userId={}, projectIds={}", userId, projectId);
        if (userId == null && projectId == null) {
            throw new OwnerIdNotPresentException(ErrorType.OWNER_ID_NOT_PRESENT);
        }

        if (userId != null && projectId != null) {
            throw new MoreOneOwnerException(ErrorType.MORE_ONE_OWNER);
        }

        validateOwner(userId);
        validateOwner(projectId);
    }

    public void validateProject(Long projectId) {
        log.debug("Start project validation with id={}", projectId);
        validateOwner(projectId);

        ProjectDto project = projectServiceClient.getProject(projectId);
        if (project == null) {
            throw new EntityNotFoundException("Project id={} not found", projectId);
        }

        if (project.status() == ProjectStatus.CANCELLED) {
            throw new EntityCancelledException("Project id={} was cancelled", projectId);
        }
    }

    public void validateUser(Long userId) {
        log.debug("Start user validation with id={}", userId);
        validateOwner(userId);

        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("User id={} not found", userId);
        }
    }

    public void validateOwner(Long ownerId) {
        log.debug("Start owner validation with id={}", ownerId);
        long currentId = userContext.getUserId();
        if (ownerId != null && currentId != ownerId) {
            throw new NotResourceOwnerException("Id={} is not owner account with id={}", currentId, ownerId);
        }
    }

    public void validateAccountStatus(Account account) {
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new EntityAlreadyClosedException("Failed. Cause: account id={} is already closed", account.getId());
        }

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new EntityAlreadyBlockedException("Failed. Cause: account id={} is already blocked", account.getId());
        }
    }
}
