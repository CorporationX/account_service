package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.account.Status;
import faang.school.accountservice.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateOpenAccount(Account account) {
        checkInputAuthorOrProject(account);

        if (account.getUserId() != null) {
            checkUserExists(account.getUserId());
        }

        if (account.getProjectId() != null) {
            checkProjectExists(account.getProjectId());
        }
    }

    public void validateCloseAccount(Account account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new ValidationException("Only active accounts can be closed");
        }
    }

    public void checkInputAuthorOrProject(Account account) {
        Long userId = account.getUserId();
        Long projectId = account.getProjectId();

        if (userId == null && projectId == null) {
            throw new ValidationException("UserId or projectId must be filled in");
        }

        if (userId != null && projectId != null) {
            throw new ValidationException("Specify either userId or projectId to create a post");
        }
    }

    private void checkUserExists(Long userId) {
        try {
            if (userServiceClient.getUser(userId) != null) {
                return;
            }
        } catch (RuntimeException ignore) {
            log.warn("Пользователь {} не был найден в userService", userId);
        }

        throw new ValidationException("User id=%s not exist", userId);
    }

    private void checkProjectExists(Long projectId) {
        try {
            if (projectServiceClient.getProject(projectId) != null) {
                return;
            }
        } catch (RuntimeException ignore) {
            log.warn("Проект {} не был найден в projectService", projectId);
        }

        throw new ValidationException("Project id=%s not exist", projectId);
    }
}
