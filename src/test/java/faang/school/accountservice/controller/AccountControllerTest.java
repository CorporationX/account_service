package faang.school.accountservice.controller;

import faang.school.accountservice.data.AccountTestData;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountValidator accountValidator;

    private AccountDto dto;

    @BeforeEach
    void setUp() {
        var testData = new AccountTestData();
        dto = testData.getAccountDto();
    }

    @Nested
    class PositiveTests {
        @Test
        void openAccountTest() {
            assertDoesNotThrow(() -> accountController.open(dto));

            verify(accountValidator).validateCreationStatus(dto);
            verify(accountValidator).validateAccountOwners(dto);
            verify(accountService).open(dto);
        }

        @Test
        void getByIdTest() {
            assertDoesNotThrow(() -> accountController.getById(1L));

            verify(accountService).getAccountById(anyLong());
        }

        @Test
        void getByNumberTest() {
            assertDoesNotThrow(() -> accountController.getByNumber("54534"));

            verify(accountService).getAccountByNumber(anyString());
        }

        @Test
        void changeStatusTest() {
            assertDoesNotThrow(() -> accountController.changeStatus(1L, AccountStatus.ACTIVE));

            verify(accountService).changeStatus(anyLong(), any(AccountStatus.class));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        void shouldNotOpenAccountWhenValidationCreationStatusFailsTest() {
            doThrow(DataValidationException.class).when(accountValidator).validateCreationStatus(any(AccountDto.class));

            assertThrows(DataValidationException.class, () -> accountController.open(dto));

            verify(accountValidator, times(0)).validateAccountOwners(dto);
            verify(accountService, times(0)).open(dto);
        }

        @Test
        void shouldNotOpenAccountWhenValidationAccountOwnersFailsTest() {
            doThrow(DataValidationException.class).when(accountValidator).validateAccountOwners(any(AccountDto.class));

            assertThrows(DataValidationException.class, () -> accountController.open(dto));

            verify(accountValidator).validateCreationStatus(dto);
            verify(accountService, times(0)).open(dto);
        }
    }
}