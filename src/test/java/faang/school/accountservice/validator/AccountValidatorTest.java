package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.exception.account.AccountHasBeenUpdateException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private AccountValidator accountValidator;

    private Account openAccount;
    private UserDto userDto;
    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() throws Exception {
        openAccount = Account.builder()
                .userId(1L)
                .projectId(1L)
                .type(AccountType.BUSINESS)
                .currency(Currency.RUB)
                .build();

        userDto = new UserDto(
                openAccount.getUserId(),
                "Mike",
                "test@email.com");

        projectDto = new ProjectDto(
                openAccount.getProjectId(),
                "Project Name");
    }

    @Test
    public void testValidateOpenAccountUser() {
        openAccount.setProjectId(null);
        when(userServiceClient.getUser(openAccount.getUserId())).thenReturn(userDto);
        accountValidator.validateOpenAccount(openAccount);

        verify(userServiceClient).getUser(openAccount.getUserId());
        verify(projectServiceClient, times(0)).getProject(anyLong());
    }

    @Test
    public void testValidateOpenAccountProject() {
        openAccount.setUserId(null);
        when(projectServiceClient.getProject(openAccount.getProjectId())).thenReturn(projectDto);
        accountValidator.validateOpenAccount(openAccount);

        verify(projectServiceClient).getProject(openAccount.getProjectId());
        verify(userServiceClient, times(0)).getUser(anyLong());
    }

    @Test
    public void testValidateOpenAccountEmptyInput() {
        openAccount.setUserId(null);
        openAccount.setProjectId(null);

        assertThrows(ValidationException.class, () -> accountValidator.validateOpenAccount(openAccount));

        verify(projectServiceClient, times(0)).getProject(anyLong());
        verify(userServiceClient, times(0)).getUser(anyLong());
    }

    @Test
    public void testValidateOpenAccountUserAndProject() {
        assertThrows(ValidationException.class, () -> accountValidator.validateOpenAccount(openAccount));

        verify(projectServiceClient, times(0)).getProject(anyLong());
        verify(userServiceClient, times(0)).getUser(anyLong());
    }

    @Test
    public void testValidateNotActiveAccountFail() {
        openAccount.setStatus(AccountStatus.CLOSED);
        assertThrows(ValidationException.class, () -> accountValidator.validateNotActiveAccount(openAccount));
    }

    @Test
    public void testValidateNotActiveAccount() {
        openAccount.setStatus(AccountStatus.ACTIVE);
        assertDoesNotThrow(() -> accountValidator.validateNotActiveAccount(openAccount));
    }
}
