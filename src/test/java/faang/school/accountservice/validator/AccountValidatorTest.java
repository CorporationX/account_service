package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.ProjectDto;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.DataValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {

    private static final String ACCOUNT_NUMBER = "123456789123456";
    private static final BigDecimal ACCOUNT_BALANCE = BigDecimal.valueOf(1000);
    private static final BigDecimal LOW_SUMMA = BigDecimal.valueOf(500);
    private static final BigDecimal BIG_SUMMA = BigDecimal.valueOf(2000);
    private static final Long OWNER_USER_ID = 25L;

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private AccountValidator accountValidator;


    @Test
    @DisplayName("Validate account balance when Ok")
    public void accountBalanceValidateWhenOk() {
        accountValidator.accountBalanceValidate(ACCOUNT_NUMBER, LOW_SUMMA, ACCOUNT_BALANCE);
    }

    @Test
    @DisplayName("Validate account balance when summa for writeoff to big")
    public void accountBalanceValidateWhenSummaTooBig() {
        String errMessage = String.format("There are not enough funds on account %s for debiting", ACCOUNT_NUMBER);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                accountValidator.accountBalanceValidate(ACCOUNT_NUMBER, BIG_SUMMA, ACCOUNT_BALANCE));

        assertEquals(errMessage, exception.getMessage());
    }

    @Test
    @DisplayName("When the owner is a user and it exists")
    public void whenOwnerUserAndExists() {
        AccountDto accountDto = AccountDto.builder()
                .ownerId(OWNER_USER_ID)
                .type(AccountType.CURRENT_INDIVIDUALS)
                .build();
        UserDto userDto = UserDto.builder()
                .id(OWNER_USER_ID)
                .build();

        when(userServiceClient.getUser(OWNER_USER_ID)).thenReturn(userDto);

        accountValidator.accountOwnerValidate(accountDto);
        Mockito.verify(userServiceClient).getUser(OWNER_USER_ID);
    }

    @Test
    @DisplayName("When the owner is a user and it not exists")
    public void whenOwnerUserAndNotExists() {
        AccountDto accountDto = AccountDto.builder()
                .ownerId(OWNER_USER_ID)
                .type(AccountType.CURRENT_INDIVIDUALS)
                .build();

        when(userServiceClient.getUser(OWNER_USER_ID)).thenReturn(null);

        accountValidator.accountOwnerValidate(accountDto);
        Mockito.verify(userServiceClient).getUser(OWNER_USER_ID);
    }

    @Test
    @DisplayName("When the owner is a project and it exists")
    public void whenOwnerProjectAndExists() {
        AccountDto accountDto = AccountDto.builder()
                .ownerId(OWNER_USER_ID)
                .type(AccountType.CURRENT_LEGAL)
                .build();
        ProjectDto projectDto = ProjectDto.builder()
                .id(OWNER_USER_ID)
                .build();

        when(projectServiceClient.getProject(OWNER_USER_ID)).thenReturn(projectDto);

        accountValidator.accountOwnerValidate(accountDto);
        Mockito.verify(projectServiceClient).getProject(OWNER_USER_ID);
    }

    @Test
    @DisplayName("When the owner is a project and it not exists")
    public void whenOwnerProjectAndNotExists() {
        AccountDto accountDto = AccountDto.builder()
                .ownerId(OWNER_USER_ID)
                .type(AccountType.CURRENT_LEGAL)
                .build();

        when(projectServiceClient.getProject(OWNER_USER_ID)).thenReturn(null);

        accountValidator.accountOwnerValidate(accountDto);
        Mockito.verify(projectServiceClient).getProject(OWNER_USER_ID);
    }

    @Test
    @DisplayName("When status is not CURRENT for operation with account")
    public void whenStatusIsNotCurrent() {
        String errMessage = "Account should be current for operations";

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                accountValidator.accountStatusValidate(AccountStatus.CLOSED));

        assertEquals(errMessage, exception.getMessage());
    }
}