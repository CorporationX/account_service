package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.project.ProjectContext;
import faang.school.accountservice.config.context.user.UserContext;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountValidator {

    private final UserContext userContext;
    private final ProjectContext projectContext;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateOwner(AccountDto accountDto) {
        if (accountDto.getOwnerType() == OwnerType.USER) {
            if (userContext.getUserId() != accountDto.getOwnerId()) {
                throw new DataValidationException("Access denied: User ID does not match the owner ID of the account");
            }
            if (userServiceClient.getUser(accountDto.getOwnerId()) == null) {
                throw new DataValidationException("The owner of the payment account must be an existing user in the system");
            }
        }

        if (accountDto.getOwnerType() == OwnerType.PROJECT) {
            if (projectContext.getProjectId() != accountDto.getOwnerId()) {
                throw new DataValidationException("Access denied: Project ID does not match the owner ID of the account");
            }
            if (projectServiceClient.getProject(accountDto.getOwnerId()) == null) {
                throw new DataValidationException("The owner of the billing account must be an existing project in the system");
            }
        }
    }

}