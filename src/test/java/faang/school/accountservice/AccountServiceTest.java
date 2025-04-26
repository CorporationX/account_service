package faang.school.accountservice;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.AccountTypeDto;
import faang.school.accountservice.dto.CurrencyDto;
import faang.school.accountservice.dto.OwnerTypeDto;
import faang.school.accountservice.dto.AccountStatusDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.accountservice.messages.ErrorMessages.ACCOUNT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Spy
    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Mock
    private AccountRepository accountRepository;

    private final String accountNumber = "18457248124521";
    private final Account account = Account.builder()
            .accountNumber(accountNumber)
            .accountType(AccountType.PERSONAL_ACCOUNT)
            .ownerType(OwnerType.USER)
            .ownerId(1L)
            .build();

    private final AccountRequestDto accountRequest = AccountRequestDto.builder()
            .ownerId(1L)
            .ownerType(OwnerTypeDto.USER)
            .currency(CurrencyDto.USD)
            .accountType(AccountTypeDto.PERSONAL_ACCOUNT)
            .description("description")
            .build();

    @Test
    public void testGetAccount_noAccount() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccount(accountNumber)
        );

        assertEquals(ACCOUNT_NOT_FOUND.formatted(accountNumber), exception.getMessage());
    }

    @Test
    public void testGetAccount_accountFound() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.of(account));
        AccountResponseDto result = accountService.getAccount(accountNumber);
        assertEquals(accountNumber, result.getAccountNumber());
    }

    @Test
    public void testCreateAccount_createdSuccessfully() {
        when(accountRepository.save(any())).thenReturn(account);
        accountService.createAccount(accountRequest);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(captor.capture());

        Account result = captor.getValue();
        assertEquals(accountRequest.getOwnerId(), result.getOwnerId());
        assertEquals(accountRequest.getOwnerType().name(), result.getOwnerType().name());
        assertEquals(accountRequest.getCurrency().name(), result.getCurrency().name());
        assertEquals(accountRequest.getAccountType().name(), result.getAccountType().name());
        assertEquals(accountRequest.getDescription(), result.getDescription());
        assertEquals(AccountStatus.ACTIVE, result.getAccountStatus());
    }

    @Test
    public void testBlockAccount_noAccount() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> accountService.blockAccount(accountNumber)
        );

        assertEquals(ACCOUNT_NOT_FOUND.formatted(accountNumber), exception.getMessage());
    }

    @Test
    public void testBlockAccount_blocked() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.of(account));

        AccountResponseDto result = accountService.blockAccount(accountNumber);

        assertEquals(AccountStatusDto.BLOCKED, result.getAccountStatus());
    }

    @Test
    public void testCloseAccount_noAccount() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> accountService.closeAccount(accountNumber)
        );

        assertEquals(ACCOUNT_NOT_FOUND.formatted(accountNumber), exception.getMessage());
    }

    @Test
    public void testCloseAccount_blocked() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.of(account));

        AccountResponseDto result = accountService.closeAccount(accountNumber);

        assertEquals(AccountStatusDto.CLOSED, result.getAccountStatus());
    }
}
