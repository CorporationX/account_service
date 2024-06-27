package faang.school.accountservice.service.account;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.data.AccountTestData;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceValidatorTest {
    @InjectMocks
    private AccountServiceValidator validator;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new AccountTestData().getAccountEntity();
    }

    @Nested
    class PositiveTests {
        @DisplayName("shouldn't throw exception when user with passed userId exists and projectId is null")
        @Test
        void validateOwnerExistenceByUserTest() {
            when(userServiceClient.existsById(anyLong())).thenReturn(true);

            assertDoesNotThrow(() -> validator.validateOwnerExistence(1L, null));

            verifyNoInteractions(projectServiceClient);
        }

        @DisplayName("shouldn't throw exception when project with passed projectId exists and userId is null")
        @Test
        void validateOwnerExistenceByProjectTest() {
            when(projectServiceClient.existsById(anyLong())).thenReturn(true);

            assertDoesNotThrow(() -> validator.validateOwnerExistence(null, 1L));

            verifyNoInteractions(userServiceClient);
        }

        @ParameterizedTest
        @EnumSource(value = AccountStatus.class, names = {"CLOSED"}, mode = EnumSource.Mode.EXCLUDE)
        void validateStatusBeforeUpdate(AccountStatus status) {
            account.setStatus(status);

            assertDoesNotThrow(() -> validator.validateStatusBeforeUpdate(account));
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when user with passed userId doesn't exist and projectId is null")
        @Test
        void validateOwnerExistenceByUserTest() {
            when(userServiceClient.existsById(anyLong())).thenReturn(false);

            assertThrows(DataValidationException.class, () -> validator.validateOwnerExistence(1L, null));

            verifyNoInteractions(projectServiceClient);
        }

        @DisplayName("shouldn't throw exception when project with passed projectId doesn't exist and userId is null")
        @Test
        void validateOwnerExistenceByProjectTest() {
            when(projectServiceClient.existsById(anyLong())).thenReturn(false);

            assertThrows(DataValidationException.class, () -> validator.validateOwnerExistence(null, 1L));

            verifyNoInteractions(userServiceClient);
        }

        @ParameterizedTest
        @EnumSource(value = AccountStatus.class, names = {"CLOSED"})
        void validateStatusBeforeUpdate(AccountStatus status) {
            account.setStatus(status);

            assertThrows(DataValidationException.class, () -> validator.validateStatusBeforeUpdate(account));
        }
    }
}