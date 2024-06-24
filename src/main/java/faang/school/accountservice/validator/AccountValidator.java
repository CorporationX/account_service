package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void accountBalanceValidate(String number, BigDecimal summa, BigDecimal balance) {
        if (balance.compareTo(summa) < 0) {
            String errMessage = String.format("There are not enough funds on account %s for debiting", number);
            log.error(errMessage, number);
            throw new DataValidationException(errMessage);
        }
    }

    public void accountOwnerValidate(AccountDto accountDto) {
        if (accountDto.getType().equals(AccountType.CURRENT_LEGAL)) {
            projectServiceClient.getProject(accountDto.getOwnerId());
        } else {
            userServiceClient.getUser(accountDto.getOwnerId());
        }
    }

    public void accountStatusValidate(AccountStatus accountStatus) {
        if (!accountStatus.equals(AccountStatus.CURRENT)) {
            String errMessage = "Account should be current for operations";
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }
    }
}
