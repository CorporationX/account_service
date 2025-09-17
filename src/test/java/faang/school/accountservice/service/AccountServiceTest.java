package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.EntityAlreadyBlockedException;
import faang.school.accountservice.exception.EntityAlreadyClosedException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class AccountServiceTest {
    @InjectMocks
    private AccountService service;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountValidator accountValidator;
    @Spy
    private AccountMapperImpl mapper;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final long USER_ID = 2;
    private static final long PROJECT_ID = 1;
    private static final String ACCOUNT_NUMBER = "12345678912345";

    @Test
    @DisplayName("Успешное создание счета")
    void positive_shouldCreateAccount() {
        CreateAccountDto createAccountDto = createCreateAccountDto();
        when(accountRepository.save(accountCaptor.capture()))
                .thenAnswer(invocation -> {
                    var account = accountCaptor.getValue();
                    account.setId(ACCOUNT_ID);
                    return account;
                });

        AccountDto actual = service.create(createAccountDto);

        verify(accountRepository, times(1)).save(accountCaptor.capture());
        assertEquals(ACCOUNT_ID, actual.id());
        assertEquals(USER_ID, actual.userId());
        assertEquals(AccountStatus.ACTIVE, actual.status());
        assertNotNull(actual.number());
        assertNull(actual.closedAt());
        assertNull(actual.projectId());
    }

    @Test
    @DisplayName("Успешное получение счета по id")
    void positive_shouldFindAccountById() {
        Account account = createExistsAccount(USER_ID, null, AccountStatus.ACTIVE);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        AccountDto expected = mapper.toDto(account);

        AccountDto actual = service.findById(ACCOUNT_ID);

        verify(accountRepository, times(1)).findById(ACCOUNT_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное получение счета по номеру")
    void positive_shouldFindAccountByNumber() {
        Account account = createExistsAccount(USER_ID, null, AccountStatus.ACTIVE);
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        AccountDto expected = mapper.toDto(account);

        AccountDto actual = service.findByNumber(ACCOUNT_NUMBER);

        verify(accountRepository, times(1)).findByNumber(ACCOUNT_NUMBER);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное получение списка счетов юзера")
    void positive_shouldFindAccountsByUserId() {
        Account account = createExistsAccount(USER_ID, null, AccountStatus.ACTIVE);
        when(accountRepository.findByUserId(USER_ID)).thenReturn(List.of(account));
        List<AccountDto> expected = List.of(mapper.toDto(account));

        List<AccountDto> actual = service.findByUserId(USER_ID);

        verify(accountRepository, times(1)).findByUserId(USER_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное получение списка счетов проекта")
    void positive_shouldFindAccountsByProjectId() {
        Account account = createExistsAccount(null, PROJECT_ID, AccountStatus.ACTIVE);
        when(accountRepository.findByProjectId(PROJECT_ID)).thenReturn(List.of(account));
        List<AccountDto> expected = List.of(mapper.toDto(account));

        List<AccountDto> actual = service.findByProjectId(PROJECT_ID);

        verify(accountRepository, times(1)).findByProjectId(PROJECT_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешная блокировка счета")
    void positive_shouldBlockAccount() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(createExistsAccount(USER_ID, null, AccountStatus.ACTIVE)));

        service.block(ACCOUNT_ID);
    }

    @Test
    @DisplayName("Успешное закрытие счета")
    void positive_shouldCloseAccount() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(createExistsAccount(USER_ID, null, AccountStatus.ACTIVE)));

        service.close(ACCOUNT_ID);
    }

    @Test
    @DisplayName("Ошибка получения счета по id - счет не найден")
    void negative_whenAccountNotFoundById_throwsError() {
        String expectedMessage = "Account not found by id=" + ACCOUNT_ID;
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        verify(accountRepository, never()).findById(any(UUID.class));
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                      () -> service.findById(ACCOUNT_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка получения счета по номеру - счет не найден")
    void negative_whenAccountNotFoundByNumber_throwsError() {
        String expectedMessage = "Account not found by number=" + ACCOUNT_NUMBER;
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        verify(accountRepository, never()).findByNumber(anyString());
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.findByNumber(ACCOUNT_NUMBER)).getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @DisplayName("Ошибка блокировки счета - счет не найден")
    void negative_whenAccountNotFound_notBlockAndThrowsError() {
        String expectedMessage = "Account not found by id=" + ACCOUNT_ID;
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.block(ACCOUNT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка блокировки счета - счет уже закрыт")
    void negative_whenAccountAlreadyClosed_notBlockAndThrowsError() {
        String expectedMessage = String.format("Failed to block account id=%s, it's already closed", ACCOUNT_ID);
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(createExistsAccount(USER_ID, null, AccountStatus.CLOSED)));

        String actualMessage = assertThrows(EntityAlreadyClosedException.class,
                                            () -> service.block(ACCOUNT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка блокировки счета - счет уже заблокирован")
    void negative_whenAccountAlreadyBlocked_notBlockAndThrowsError() {
        String expectedMessage = String.format("Account id=%s, is already blocked", ACCOUNT_ID);
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(createExistsAccount(USER_ID, null, AccountStatus.FROZEN)));

        String actualMessage = assertThrows(EntityAlreadyBlockedException.class,
                                            () -> service.block(ACCOUNT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка закрытия счета - счет не найден")
    void negative_whenAccountNotFound_notCloseAndThrowsError() {
        String expectedMessage = "Account not found by id=" + ACCOUNT_ID;
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.close(ACCOUNT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка закрытия счета - счет уже закрыт")
    void negative_whenAccountAlreadyClosed_notCloseAndThrowsError() {
        String expectedMessage = String.format("Account id=%s is already closed", ACCOUNT_ID);
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(createExistsAccount(USER_ID, null, AccountStatus.CLOSED)));

        String actualMessage = assertThrows(EntityAlreadyClosedException.class,
                                            () -> service.close(ACCOUNT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    // ------------------------------

    private CreateAccountDto createCreateAccountDto() {
        return new CreateAccountDto(USER_ID, null, AccountType.PERSONAL_CURRENT, Currency.RUB);
    }

    private Account createExistsAccount(Long userId, Long projectId, AccountStatus status) {
        return Account.builder()
                .id(ACCOUNT_ID)
                .number(ACCOUNT_NUMBER)
                .userId(userId)
                .projectId(projectId)
                .accountType(AccountType.PERSONAL_CURRENT)
                .currency(Currency.RUB)
                .status(status)
                .build();
    }
}