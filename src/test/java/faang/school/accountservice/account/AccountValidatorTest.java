package faang.school.accountservice.account;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.entity.account.enums.AccountStatus;
import faang.school.accountservice.entity.account.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.validator.account.AccountValidator;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountValidatorTest {

    @Mock
    private UserServiceClient userClient;
    @Mock
    private ProjectServiceClient projectClient;

    @InjectMocks
    private AccountValidator accountValidator;

    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        accountDto = getAccountDto();
    }

    @Test
    void testCheckOpeningWithException() {
        accountDto.setPaymentNumber("12345");

        assertThrows(AccountNotFoundException.class, () -> accountValidator.checkOpening(accountDto));
    }

    @Test
    void testCheckOpeningPositive() {
        assertDoesNotThrow(() -> accountValidator.checkOpening(accountDto));
    }

    @Test
    void testCheckUserIdWithException() {
        Long userId = 1L;
        doThrow(FeignException.class).when(userClient).getUser(userId);

        assertThrows(AccountNotFoundException.class, () -> accountValidator.checkUserId(userId));
    }

    @Test
    void testCheckUserIdPositive() {
        Long userId = 1L;
        when(userClient.getUser(userId)).thenReturn(UserDto.builder().build());

        assertDoesNotThrow(() -> accountValidator.checkUserId(userId));
    }

    @Test
    void testCheckProjectIdWithException() {
        Long projectId = 1L;
        doThrow(FeignException.class).when(projectClient).getProject(projectId);

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> accountValidator.checkProjectId(projectId));
    }

    @Test
    void testCheckProjectIdPositive() {
        Long projectId = 1L;
        when(projectClient.getProject(projectId)).thenReturn(new ProjectDto());

        assertDoesNotThrow(() -> accountValidator.checkProjectId(projectId));
    }

    public AccountDto getAccountDto() {
        return AccountDto.builder()
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.RUB)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
