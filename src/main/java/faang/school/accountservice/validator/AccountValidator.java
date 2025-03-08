package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.ProjectDto;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.exception.DataNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    private static final String EXCEPTION_MESSAGE = "There is no such %s with id %d";
    private static final String PROJECT = "project";
    private static final String USER = "user";

    public void validateAccountOwner(AccountDto accountDto) {
        if (accountDto.getOwnerType() == AccountOwnerType.PROJECT) {
            checkIsProjectExist(accountDto.getOwnerId());
        } else {
            checkIsUserExist(accountDto.getOwnerId());
        }
    }

    private void checkIsUserExist(@NotBlank(message = "Owner id can't be empty.") BigInteger ownerId) {
        UserDto user = userServiceClient.getUser(ownerId.longValue());
        if (user == null) {
            throw new DataNotFoundException(String.format(EXCEPTION_MESSAGE, USER, ownerId));
        }
    }

    private void checkIsProjectExist(@NotBlank(message = "Owner id can't be empty.") BigInteger ownerId) {
        ProjectDto project = projectServiceClient.getProject(ownerId.longValue());
        if (project == null) {
            throw new DataNotFoundException(String.format(EXCEPTION_MESSAGE, PROJECT, ownerId));
        }
    }

}
