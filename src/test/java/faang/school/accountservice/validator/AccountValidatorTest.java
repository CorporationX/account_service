package faang.school.accountservice.validator;

import faang.school.accountservice.data.AccountTestData;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.exception.message.AccountExceptionMessage.NO_OWNER_EXCEPTION;
import static faang.school.accountservice.exception.message.AccountExceptionMessage.TWO_OWNERS_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {
    private final AccountValidator validator = new AccountValidator();
    private AccountDto dto;

    @BeforeEach
    void setUp() {
        var testData = new AccountTestData();
        dto = testData.getAccountDto();
    }

    @Nested
    class PositiveTests {
        @Test
        void shouldNotThrowExceptionWhenDtoHasOnlyUserOwnerTest() {
            dto.setOwnerUserId(1L);
            dto.setOwnerProjectId(null);

            assertDoesNotThrow(() -> validator.validateAccountOwners(dto));
        }

        @Test
        void shouldNotThrowExceptionWhenDtoHasOnlyProjectOwnerTest() {
            dto.setOwnerUserId(null);
            dto.setOwnerProjectId(1L);

            assertDoesNotThrow(() -> validator.validateAccountOwners(dto));
        }

        @ParameterizedTest
        @EnumSource(value = AccountStatus.class, names = {"CLOSED"}, mode = EnumSource.Mode.EXCLUDE)
        void shouldNotThrowExceptionWhenDtoHasntClosedStatusTest(AccountStatus status) {
            dto.setStatus(status);

            assertDoesNotThrow(() -> validator.validateCreationStatus(dto));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        void shouldThrowExceptionWhenDtoHasNoOwnersTest() {
            dto.setOwnerUserId(null);

            DataValidationException e = assertThrows(DataValidationException.class,
                    () -> validator.validateAccountOwners(dto));

            assertEquals(NO_OWNER_EXCEPTION.getMessage(), e.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenDtoHasTwoOwnersTest() {
            dto.setOwnerProjectId(1L);

            DataValidationException e = assertThrows(DataValidationException.class,
                    () -> validator.validateAccountOwners(dto));

            assertEquals(TWO_OWNERS_EXCEPTION.getMessage(), e.getMessage());
        }

        @ParameterizedTest
        @EnumSource(value = AccountStatus.class, names = {"CLOSED"})
        void shouldThrowExceptionWhenDtoHasClosedStatusTest(AccountStatus status) {
            dto.setStatus(status);

            assertThrows(DataValidationException.class, () -> validator.validateCreationStatus(dto));
        }
    }
}