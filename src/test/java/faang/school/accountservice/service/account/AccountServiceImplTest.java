package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.account.AccountOwnershipException;
import faang.school.accountservice.exception.account.IllegalStatusTransitionException;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    private final String newAccountNumber = "12345678912";
    private final String existingAccountNumber = "12345678901";

    private CreateAccountDto getNewUserAccountDto(AccountStatus accountStatus) {
        return new CreateAccountDto(
                newAccountNumber,
                1L,
                null,
                AccountType.PERSONAL,
                Currency.EUR,
                accountStatus
        );
    }

    private Account getAccount() {
        return Account.builder()
                .id(111L)
                .accountNumber(existingAccountNumber)
                .userId(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .currency(Currency.EUR)
                .build();
    }

    @Test
    void createDoesNotSaveOnValidationError() {
        CreateAccountDto createDto = getNewUserAccountDto(null);
        doThrow(new AccountOwnershipException("bad owner"))
                .when(accountValidator)
                .validateCreate(createDto);

        assertThrows(AccountOwnershipException.class, () -> accountService.create(createDto));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void createMapsToEntityAppliesDefaultsAndSavesIfValid() {
        CreateAccountDto createDto = getNewUserAccountDto(null);
        doNothing().when(accountValidator).validateCreate(createDto);

        accountService.create(createDto);

        verify(accountMapper).toAccount(createDto);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account result = accountArgumentCaptor.getValue();
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
    }

    @Test
    void createRespectsPassedAccountStatus() {
        AccountStatus accountStatus = AccountStatus.FROZEN;
        CreateAccountDto createDto = getNewUserAccountDto(accountStatus);
        doNothing().when(accountValidator).validateCreate(createDto);

        accountService.create(createDto);

        verify(accountMapper).toAccount(createDto);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account result = accountArgumentCaptor.getValue();
        assertEquals(accountStatus, result.getStatus());
    }

    @Test
    void getAccountByIdCallsRepo() {
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(Account.class)));

        accountService.getAccountById(accountId);

        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountByIdThrowsIfNotFound() {
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(accountId));

        verify(accountRepository).findById(accountId);
    }

    @Test
    void updateAccountStatusUpdatesStatusInValidCaseAndReturns() {
        Account account = getAccount();
        Long accountId = account.getId();
        AccountStatus newStatus = AccountStatus.FROZEN;
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);

        AccountDto result = accountService.updateAccountStatus(accountId, newStatus);

        verify(accountRepository).saveAndFlush(accountArgumentCaptor.capture());
        assertEquals(newStatus, accountArgumentCaptor.getValue().getStatus());
        assertEquals(accountId, result.id());
        assertEquals(account.getAccountNumber(), result.accountNumber());
        assertEquals(newStatus, result.status());
    }

    @Test
    void updateAccountStatusDoesNotUpdateInvalidTransition() {
        Long id = 99L;
        when(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mock(Account.class)));
        doThrow(new IllegalStatusTransitionException("bad transition"))
                .when(accountValidator)
                .validateStatusTransition(any(), any());

        assertThrows(IllegalStatusTransitionException.class,
                () -> accountService.updateAccountStatus(id, AccountStatus.CLOSED)
        );

        verify(accountRepository).findById(id);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void updateAccountStatusThrowsIfAccountNotFound() {
        Long id = 99L;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accountService.updateAccountStatus(id, AccountStatus.FROZEN));

        verify(accountRepository).findById(id);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void updateAccountStatusSetsClosedAt() {
        Account account = getAccount();
        Long accountId = account.getId();
        AccountStatus newStatus = AccountStatus.CLOSED;
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);

        AccountDto result = accountService.updateAccountStatus(accountId, newStatus);

        verify(accountRepository).saveAndFlush(account);
        assertEquals(accountId, result.id());
        assertEquals(account.getAccountNumber(), result.accountNumber());
        assertEquals(newStatus, result.status());
        assertNotNull(result.closedAt());
        assertInstanceOf(LocalDateTime.class, result.closedAt());
    }

    @Test
    void updateAccountStatusPropagatesOptimisticLockingFailure() {
        Account account = getAccount();
        Long accountId = account.getId();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(any(Account.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Account.class, accountId));

        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> accountService.updateAccountStatus(accountId, AccountStatus.FROZEN)
        );
    }
}