package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AccountController")
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Nested
    @DisplayName("Тесты для метода getAccount")
    class GetAccountTests {

        @Test
        @DisplayName("Успешное получение счета по ID")
        void givenValidId_WhenGetAccount_ThenReturnsAccount() {
            Long accountId = 1L;
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .createdAt(Instant.now())
                    .build();

            when(accountService.getAccount(accountId)).thenReturn(accountViewDto);

            ResponseEntity<AccountViewDto> response = accountController.getAccount(accountId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accountViewDto, response.getBody());
            verify(accountService).getAccount(accountId);
        }

        @Test
        @DisplayName("Получение счета с несуществующим ID")
        void givenInvalidId_WhenGetAccount_ThenThrowsNotFoundException() {
            Long accountId = 999L;
            when(accountService.getAccount(accountId))
                    .thenThrow(new AccountNotFoundException("Account not found with id: 999"));

            assertThrows(AccountNotFoundException.class, () -> accountController.getAccount(accountId));
            verify(accountService).getAccount(accountId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода getAccountsByOwner")
    class GetAccountsByOwnerTests {

        @Test
        @DisplayName("Успешное получение счетов по владельцу")
        void givenValidOwnerParams_WhenGetAccountsByOwner_ThenReturnsPage() {
            OwnerType ownerType = OwnerType.USER;
            Long ownerId = 1L;
            PageRequest pageable = PageRequest.of(0, 20);
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(ownerType)
                    .ownerId(ownerId)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            Page<AccountViewDto> page = new PageImpl<>(Collections.singletonList(accountViewDto), pageable, 1);

            when(accountService.getAccountsByOwner(ownerType, ownerId, pageable)).thenReturn(page);

            ResponseEntity<Page<AccountViewDto>> response = accountController.getAccountsByOwner(ownerType, ownerId, pageable);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(page, response.getBody());
            verify(accountService).getAccountsByOwner(ownerType, ownerId, pageable);
        }
    }

    @Nested
    @DisplayName("Тесты для метода openAccount")
    class OpenAccountTests {

        @Test
        @DisplayName("Успешное создание нового счета")
        void givenValidAccountData_WhenOpenAccount_ThenReturnsCreatedAccount() {
            AccountCreateDto createDto = AccountCreateDto.builder()
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountService.openAccount(createDto)).thenReturn(accountViewDto);

            ResponseEntity<AccountViewDto> response = accountController.openAccount(createDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accountViewDto, response.getBody());
            verify(accountService).openAccount(createDto);
        }
    }

    @Nested
    @DisplayName("Тесты для метода blockAccount")
    class BlockAccountTests {

        @Test
        @DisplayName("Успешная блокировка счета")
        void givenValidId_WhenBlockAccount_ThenReturnsBlockedAccount() {
            Long accountId = 1L;
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();

            when(accountService.blockAccount(accountId)).thenReturn(accountViewDto);

            ResponseEntity<AccountViewDto> response = accountController.blockAccount(accountId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accountViewDto, response.getBody());
            verify(accountService).blockAccount(accountId);
        }

        @Test
        @DisplayName("Блокировка счета с несуществующим ID")
        void givenInvalidId_WhenBlockAccount_ThenThrowsNotFoundException() {
            Long accountId = 999L;
            when(accountService.blockAccount(accountId))
                    .thenThrow(new AccountNotFoundException("Account not found with id: 999"));

            assertThrows(AccountNotFoundException.class, () -> accountController.blockAccount(accountId));
            verify(accountService).blockAccount(accountId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода closeAccount")
    class CloseAccountTests {

        @Test
        @DisplayName("Успешное закрытие счета")
        void givenValidId_WhenCloseAccount_ThenReturnsClosedAccount() {
            Long accountId = 1L;
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            when(accountService.closeAccount(accountId)).thenReturn(accountViewDto);

            ResponseEntity<AccountViewDto> response = accountController.closeAccount(accountId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accountViewDto, response.getBody());
            verify(accountService).closeAccount(accountId);
        }

        @Test
        @DisplayName("Закрытие счета с несуществующим ID")
        void givenInvalidId_WhenCloseAccount_ThenThrowsNotFoundException() {
            Long accountId = 999L;
            when(accountService.closeAccount(accountId))
                    .thenThrow(new AccountNotFoundException("Account not found with id: 999"));

            assertThrows(AccountNotFoundException.class, () -> accountController.closeAccount(accountId));
            verify(accountService).closeAccount(accountId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода unblockAccount")
    class UnblockAccountTests {

        @Test
        @DisplayName("Успешная разблокировка счета")
        void givenValidId_WhenUnblockAccount_ThenReturnsUnblockedAccount() {
            Long accountId = 1L;
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountService.unblockAccount(accountId)).thenReturn(accountViewDto);

            ResponseEntity<AccountViewDto> response = accountController.unblockAccount(accountId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accountViewDto, response.getBody());
            verify(accountService).unblockAccount(accountId);
        }

        @Test
        @DisplayName("Разблокировка счета с несуществующим ID")
        void givenInvalidId_WhenUnblockAccount_ThenThrowsNotFoundException() {
            Long accountId = 999L;
            when(accountService.unblockAccount(accountId))
                    .thenThrow(new AccountNotFoundException("Account not found with id: 999"));

            assertThrows(AccountNotFoundException.class, () -> accountController.unblockAccount(accountId));
            verify(accountService).unblockAccount(accountId);
        }
    }
}