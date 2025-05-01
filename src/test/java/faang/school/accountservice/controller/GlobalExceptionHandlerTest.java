package faang.school.accountservice.controller;

import faang.school.accountservice.exception.AccountAlreadyClosedException;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Nested
    @DisplayName("Тесты для метода handleAccountNotFound")
    class HandleAccountNotFoundTests {

        @Test
        @DisplayName("Обработка исключения AccountNotFoundException")
        void givenAccountNotFoundException_WhenHandleAccountNotFound_ThenReturnsNotFoundResponse() {
            AccountNotFoundException exception = new AccountNotFoundException("Account not found with id: 999");

            ResponseEntity<String> response = exceptionHandler.handleAccountNotFound(exception);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Account not found: Account not found with id: 999", response.getBody());
        }
    }

    @Nested
    @DisplayName("Тесты для метода handleAlreadyClosed")
    class HandleAlreadyClosedTests {

        @Test
        @DisplayName("Обработка исключения AccountAlreadyClosedException")
        void givenAccountAlreadyClosedException_WhenHandleAlreadyClosed_ThenReturnsBadRequestResponse() {
            AccountAlreadyClosedException exception = new AccountAlreadyClosedException("Cannot modify closed account");

            ResponseEntity<String> response = exceptionHandler.handleAlreadyClosed(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Cannot modify closed account", response.getBody());
        }
    }

    @Nested
    @DisplayName("Тесты для метода handleConflict")
    class HandleConflictTests {

        @Test
        @DisplayName("Обработка исключения AccountOperationConflictException")
        void givenAccountOperationConflictException_WhenHandleConflict_ThenReturnsConflictResponse() {
            AccountOperationConflictException exception = new AccountOperationConflictException("Account is already blocked");

            ResponseEntity<String> response = exceptionHandler.handleConflict(exception);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Account is already blocked", response.getBody());
        }
    }

    @Nested
    @DisplayName("Тесты для метода handleException")
    class HandleExceptionTests {

        @Test
        @DisplayName("Обработка общего исключения")
        void givenGenericException_WhenHandleException_ThenReturnsInternalServerErrorResponse() {
            Exception exception = new Exception("Unexpected error");

            ResponseEntity<String> response = exceptionHandler.handleException(exception);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("An internal error has occurred: Unexpected error", response.getBody());
        }
    }

    @Nested
    @DisplayName("Тесты для метода handleTypeMismatch")
    class HandleTypeMismatchTests {

        @Test
        @DisplayName("Обработка исключения MethodArgumentTypeMismatchException")
        void givenMethodArgumentTypeMismatchException_WhenHandleTypeMismatch_ThenReturnsBadRequestResponse() {
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getMessage()).thenReturn("Invalid argument type");

            ResponseEntity<String> response = exceptionHandler.handleTypeMismatch(exception);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid argument type: Invalid argument type", response.getBody());
        }
    }
}