package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestAccountDto;
import faang.school.accountservice.dto.ResponseAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.enums.AccountType;
import faang.school.accountservice.entity.enums.Currency;
import faang.school.accountservice.entity.enums.OwnerType;
import faang.school.accountservice.entity.enums.Status;
import faang.school.accountservice.exception.InvalidAccountStateException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountAction;
import faang.school.accountservice.service.account.AccountNumberGenerator;
import faang.school.accountservice.service.account.impl.AccountService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Spy
    AccountMapper accountMapper;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountNumberGenerator accountNumberGenerator;

    private final long EXISTING_ACCOUNT_ID = 1L;
    private final long NON_EXISTING_ACCOUNT_ID = 999L;
    private Account account;
    private ResponseAccountDto responseDto;
    private RequestAccountDto requestAccountDto;
    private Account accountEntity;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(EXISTING_ACCOUNT_ID)
                .status(Status.ACTIVE)
                .build();

        responseDto = ResponseAccountDto.builder()
                .id(EXISTING_ACCOUNT_ID)
                .status(Status.ACTIVE)
                .build();

        requestAccountDto = RequestAccountDto.builder()
                .ownerId(EXISTING_ACCOUNT_ID)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL)
                .currency(Currency.USD).build();

        accountEntity = Account.builder()
                .ownerId(EXISTING_ACCOUNT_ID)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL)
                .currency(Currency.USD).build();
    }

    @Test
    void get_WhenAccountExists_ReturnsResponseAccountDto() {
        when(accountRepository.findById(EXISTING_ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(responseDto);

        ResponseAccountDto result = accountService.get(EXISTING_ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.getStatus(), result.getStatus());

        verify(accountRepository, times(1)).findById(EXISTING_ACCOUNT_ID);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void get_WhenAccountNotExists_ThrowsEntityNotFoundException() {
        when(accountRepository.findById(NON_EXISTING_ACCOUNT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accountService.get(NON_EXISTING_ACCOUNT_ID));

        assertEquals("Account with id " + NON_EXISTING_ACCOUNT_ID + " does not exists", exception.getMessage());

        verify(accountRepository, times(1)).findById(NON_EXISTING_ACCOUNT_ID);
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void open_WhenRequestAccountDtoValid_CreatesNewAccount() {
        String uniqueNumber = "1234567891234567";

        when(accountMapper.toEntity(requestAccountDto)).thenReturn(accountEntity);
        when(accountNumberGenerator.generateUniqueNumber()).thenReturn(uniqueNumber);

        accountService.open(requestAccountDto);
        assertEquals(Status.ACTIVE, accountEntity.getStatus());
        assertEquals(uniqueNumber, accountEntity.getNumber());
        assertEquals(16, accountEntity.getNumber().length());

        verify(accountMapper, times(1)).toEntity(requestAccountDto);
        verify(accountRepository, times(1)).save(accountEntity);

    }

    @Test
    void block_WhenAccountExits() {
        when(accountRepository.findById(EXISTING_ACCOUNT_ID)).thenReturn(Optional.of(account));
        accountService.applyAccountAction(EXISTING_ACCOUNT_ID, AccountAction.BLOCKED);
        assertEquals(Status.BLOCKED, account.getStatus());
        verify(accountRepository, times(1)).findById(EXISTING_ACCOUNT_ID);
    }

    @Test
    void block_WhenAccountNotExists_ThrowEntityNotFoundException() {
        when(accountRepository.findById(NON_EXISTING_ACCOUNT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accountService.get(NON_EXISTING_ACCOUNT_ID));
        assertEquals("Account with id " + NON_EXISTING_ACCOUNT_ID + " does not exists", exception.getMessage());
        verify(accountRepository, times(1)).findById(NON_EXISTING_ACCOUNT_ID);
    }

    @Test
    void unblock_WhenAccountExitsAndBlocked() {
        account.setStatus(Status.BLOCKED);
        when(accountRepository.findById(EXISTING_ACCOUNT_ID)).thenReturn(Optional.of(account));
        accountService.applyAccountAction(EXISTING_ACCOUNT_ID, AccountAction.UNBLOCKED);
        assertEquals(Status.ACTIVE, account.getStatus());
        verify(accountRepository, times(1)).findById(EXISTING_ACCOUNT_ID);
    }

    @Test
    void unblock_WhenAccountIsNotBlocked_ThrowInvalidAccountStateException() {
        when(accountRepository.findById(EXISTING_ACCOUNT_ID)).thenReturn(Optional.of(account));
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class,
                () -> accountService.applyAccountAction(EXISTING_ACCOUNT_ID, AccountAction.UNBLOCKED));
        assertEquals("Account with id: " + EXISTING_ACCOUNT_ID + " already unblocked", exception.getMessage());
                assertEquals(Status.ACTIVE, account.getStatus());
        verify(accountRepository, times(1)).findById(EXISTING_ACCOUNT_ID);
    }

    @Test
    void close_WhenAccountIsOpen() {
        when(accountRepository.findById(EXISTING_ACCOUNT_ID)).thenReturn(Optional.of(account));
        accountService.applyAccountAction(EXISTING_ACCOUNT_ID, AccountAction.CLOSED);
        assertEquals(Status.CLOSED, account.getStatus());
        verify(accountRepository, times(1)).findById(EXISTING_ACCOUNT_ID);
    }

    @Test
    void close_WhenAccountAlreadyClosed_ThrowInvalidAccountStateException() {
        account.setStatus(Status.CLOSED);
        when(accountRepository.findById(EXISTING_ACCOUNT_ID)).thenReturn(Optional.of(account));
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class,
                () -> accountService.applyAccountAction(EXISTING_ACCOUNT_ID, AccountAction.CLOSED));
        assertEquals("Account with id: " + EXISTING_ACCOUNT_ID + " already closed", exception.getMessage());
        verify(accountRepository, times(1)).findById(EXISTING_ACCOUNT_ID);
    }

    @Test
    void close_WhenAccountNotExists_ThrowsEntityNotFoundException() {
        when(accountRepository.findById(NON_EXISTING_ACCOUNT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accountService.applyAccountAction(NON_EXISTING_ACCOUNT_ID, AccountAction.CLOSED));
        assertEquals("Account with id " + NON_EXISTING_ACCOUNT_ID + " does not exists", exception.getMessage());
        verify(accountRepository, times(1)).findById(NON_EXISTING_ACCOUNT_ID);
    }
}