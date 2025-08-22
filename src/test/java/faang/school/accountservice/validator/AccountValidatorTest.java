package faang.school.accountservice.validator;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.ProjectDto;
import faang.school.accountservice.dto.UserDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.ProjectStatus;
import faang.school.accountservice.exception.EntityCancelledException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.MoreOneOwnerException;
import faang.school.accountservice.exception.NotResourceOwnerException;
import faang.school.accountservice.exception.OwnerIdNotPresentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class AccountValidatorTest {
    @InjectMocks
    private AccountValidator validator;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    private static final long USER_ID = 2;
    private static final long PROJECT_ID = 1;

    @Test
    @DisplayName("Успешная валидация счета для юзера")
    void positive_whenOwnerUser_shouldValidateDto() {
        CreateAccountDto createAccountDto = createCreateAccountDto(USER_ID, null);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(createUserDto());

        validator.validateDto(createAccountDto);
    }

    @Test
    @DisplayName("Успешная валидация счета для проекта")
    void positive_whenOwnerProject_shouldValidateDto() {
        CreateAccountDto createAccountDto = createCreateAccountDto(null, PROJECT_ID);
        when(userContext.getUserId()).thenReturn(PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(createProjectDto(ProjectStatus.CREATED));

        validator.validateDto(createAccountDto);
    }

    // ------
    @Test
    void negative_whenUserAndProjectIdPresent_throwsError() {
        CreateAccountDto createAccountDto = createCreateAccountDto(USER_ID, PROJECT_ID);

        assertThrows(MoreOneOwnerException.class,
                     () -> validator.validateDto(createAccountDto));
    }

    @Test
    void negative_whenUserAndProjectIdNotPresent_throwsError() {
        CreateAccountDto createAccountDto = createCreateAccountDto(null, null);

        assertThrows(OwnerIdNotPresentException.class,
                     () -> validator.validateDto(createAccountDto));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2, null, 3",
            "null, 1, 3"
    }, nullValues = {"null"})
    void negative_whenNotAccountOwner_throwsError(Long userId, Long projectId, Long currentId) {
        CreateAccountDto createAccountDto = createCreateAccountDto(userId, projectId);
        when(userContext.getUserId()).thenReturn(currentId);

        assertThrows(NotResourceOwnerException.class,
                     () -> validator.validateDto(createAccountDto));
    }

    @Test
    void negative_whenProjectNotFound_throwsError() {
        String expectedMessage = String.format("Project id=%s not found", PROJECT_ID);
        CreateAccountDto createAccountDto = createCreateAccountDto(null, PROJECT_ID);
        when(userContext.getUserId()).thenReturn(PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(null);

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> validator.validateDto(createAccountDto)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void negative_whenProjectCancelled_throwsError() {
        CreateAccountDto createAccountDto = createCreateAccountDto(null, PROJECT_ID);
        when(userContext.getUserId()).thenReturn(PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(createProjectDto(ProjectStatus.CANCELLED));

        assertThrows(EntityCancelledException.class,
                     () -> validator.validateDto(createAccountDto));
    }

    @Test
    void negative_whenUserNotFound_throwsError() {
        String expectedMessage = String.format("User id=%s not found", USER_ID);
        CreateAccountDto createAccountDto = createCreateAccountDto(USER_ID, null);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> validator.validateDto(createAccountDto)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    // -------------------------

    private CreateAccountDto createCreateAccountDto(Long userId, Long projectId) {
        return new CreateAccountDto(userId, projectId, AccountType.PERSONAL_CURRENT, Currency.RUB);
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .build();
    }

    private ProjectDto createProjectDto(ProjectStatus status) {
        return ProjectDto.builder()
                .status(status)
                .build();
    }
}