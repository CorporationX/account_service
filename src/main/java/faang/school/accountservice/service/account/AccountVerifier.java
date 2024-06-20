package faang.school.accountservice.service.account;


import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.exception.message.CommonExceptionMessage.NON_EXISTING_PROJECT_EXCEPTION;
import static faang.school.accountservice.exception.message.CommonExceptionMessage.NON_EXISTING_USER_EXCEPTION;

@Component
@RequiredArgsConstructor
class AccountVerifier {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void verifyOwnerExistence(Long userId, Long projectId) {
        if (userId != null) {
            verifyUserExistence(userId);
        }

        if (projectId != null) {
            verifyProjectExistence(projectId);
        }
    }

    public void verifyUserExistence(long userId) {
        if (!userServiceClient.existsById(userId)) {
            throw new DataValidationException(NON_EXISTING_USER_EXCEPTION.getMessage());
        }
    }

    public void verifyProjectExistence(long projectId) {
        if (!projectServiceClient.existsById(projectId)) {
            throw new DataValidationException(NON_EXISTING_PROJECT_EXCEPTION.getMessage());
        }
    }
}
