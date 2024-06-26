package faang.school.accountservice.service.account;

import faang.school.accountservice.data.AccountTestData;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static faang.school.accountservice.enums.AccountStatus.CLOSED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Spy
    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountServiceValidator accountServiceValidator;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper mapper;

    private AccountDto dto;
    private Account entity;
    private String accountNumber;


    @BeforeEach
    void setUp() {
        var testData = new AccountTestData();
        dto = testData.getAccountDto();
        entity = testData.getAccountEntity();
        accountNumber = AccountTestData.ACCOUNT_NUMBER;
    }

    @Nested
    class PositiveTests {
        @Test
        void openTest() {
            when(mapper.toEntity(any(AccountDto.class))).thenReturn(entity);
            when(accountRepository.save(any(Account.class))).thenReturn(entity);

            assertDoesNotThrow(() -> accountService.open(dto));

            verify(mapper).toDto(entity);
        }

        @Test
        void getAccountByIdTest() {
            doReturn(entity).when(accountService).getAccountModelById(anyLong());

            assertDoesNotThrow(() -> accountService.getAccountById(1L));

            verify(mapper).toDto(entity);
        }

        @Test
        void getAccountByNumberTest() {
            doReturn(entity).when(accountService).getAccountModelByNumber(anyString());

            assertDoesNotThrow(() -> accountService.getAccountByNumber(accountNumber));

            verify(mapper).toDto(entity);
        }

        @ParameterizedTest
        @EnumSource(value = AccountStatus.class, names = {"CLOSED"}, mode = EnumSource.Mode.EXCLUDE)
        void changeStatusTest(AccountStatus status) {
            var accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
            doReturn(entity).when(accountService).getAccountModelById(anyLong());
            when(accountRepository.save(any(Account.class))).thenReturn(entity);

            assertDoesNotThrow(() -> accountService.changeStatus(1L, status));

            verify(accountRepository).save(accountArgumentCaptor.capture());
            assertEquals(status, accountArgumentCaptor.getValue().getStatus());
            verify(mapper).toDto(accountArgumentCaptor.getValue());
        }

        @ParameterizedTest
        @EnumSource(value = AccountStatus.class, names = {"CLOSED"})
        void changeStatusToClosedTest(AccountStatus status) {
            var accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
            doReturn(entity).when(accountService).getAccountModelById(anyLong());
            when(accountRepository.save(any(Account.class))).thenReturn(entity);

            assertDoesNotThrow(() -> accountService.changeStatus(1L, status));

            verify(accountRepository).save(accountArgumentCaptor.capture());

            Account argumentCaptorValue = accountArgumentCaptor.getValue();
            assertEquals(status, argumentCaptorValue.getStatus());
            assertEquals(LocalDateTime.now().withNano(0), argumentCaptorValue.getClosedAt().withNano(0));
            verify(mapper).toDto(argumentCaptorValue);
        }

        @Test
        void getAccountModelByIdTest() {
            when(accountRepository.findById(anyLong())).thenReturn(Optional.of(entity));

            assertDoesNotThrow(() -> accountService.getAccountModelById(1L));
        }

        @Test
        void getAccountModelByNumberTest() {
            when(accountRepository.findByNumber(anyString())).thenReturn(Optional.of(entity));

            assertDoesNotThrow(() -> accountService.getAccountModelByNumber(accountNumber));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        void openTest() {
            doThrow(DataValidationException.class)
                    .when(accountServiceValidator)
                    .validateOwnerExistence(any(), any());

            assertThrows(DataValidationException.class, () -> accountService.open(dto));

            verifyNoInteractions(accountRepository);
            verifyNoInteractions(mapper);
        }

        @Test
        void changeStatusTest() {
            doReturn(entity).when(accountService).getAccountModelById(anyLong());
            doThrow(DataValidationException.class)
                    .when(accountServiceValidator)
                    .validateStatusBeforeUpdate(any(Account.class));

            assertThrows(DataValidationException.class, () -> accountService.changeStatus(1L, CLOSED));

            verifyNoInteractions(accountRepository);
            verifyNoInteractions(mapper);
        }

        @Test
        void getAccountModelByIdTest() {
            when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> accountService.getAccountModelById(1L));
        }

        @Test
        void getAccountModelByNumberTest() {
            when(accountRepository.findByNumber(anyString())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> accountService.getAccountModelByNumber(accountNumber));
        }
    }
}