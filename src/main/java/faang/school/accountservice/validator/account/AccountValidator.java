package faang.school.accountservice.validator.account;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.exception.account.AccountException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final UserServiceClient userClient;
    private final ProjectServiceClient projectClient;

    public void checkOpening(AccountDto accountDto) {
        if (accountDto.getPaymentNumber().isEmpty()) {
            log.error("Payment number not empty");
            throw new AccountException("Payment number must be empty");
        }
    }

    public void checkUserId(Long id) {
        try {
            userClient.getUser(id);
        } catch (FeignException e) {
            log.error("User doesn't exist");
            throw new AccountException("User doesn't exist");
        }
    }

    public void checkProjectId(Long id) {
        try {
            projectClient.getProject(id);
        } catch (FeignException e) {
            log.error("Project doesn't exist");
            throw new AccountException("Project doesn't exist");
        }
    }
}
