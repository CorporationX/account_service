package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;
    private long id;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(id)
                .number("123456789012")
                .status(AccountStatus.ACTIVE)
                .build();
        accountDto = AccountDto.builder()
                .id(id)
                .number("123456789012")
                .status(AccountStatus.ACTIVE)
                .build();
        id = 1L;
    }

    @Test
    void getAccountById_ShouldReturnAccountDto_WhenAccountExists() {
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getAccountById(id);

        assertEquals(accountDto, result);
        verify(accountRepository).findById(id);
        verify(accountMapper).toDto(account);
    }

    @Test
    void getAccountById_ShouldThrowEntityNotFoundException_WhenAccountNotExists() {
        String correctMessage = "Account not found with id 1";
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accountService.getAccountById(id));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void openAccount_ShouldReturnAccountDto_WhenAccountIsSaved() {
        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.openAccount(accountDto);

        assertEquals(accountDto, result);
        verify(accountRepository).save(account);
    }

    @Test
    void frozeAccount_ShouldChangeAccountStatusToFrozen() {
        accountDto.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.frozeAccount(id);

        verify(accountRepository).findById(id);
        verify(accountRepository).save(account);
        assertEquals(accountDto, result);
        assertEquals(AccountStatus.FROZEN, account.getStatus());
    }

    @Test
    void blockAccount_ShouldChangeAccountStatusToBlock() {
        accountDto.setStatus(AccountStatus.BLOCK);
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.blockAccount(id);

        verify(accountRepository).findById(id);
        verify(accountRepository).save(account);
        assertEquals(accountDto, result);
        assertEquals(AccountStatus.BLOCK, account.getStatus());
    }

    @Test
    void closeAccount_ShouldChangeAccountStatusToClosed() {
        accountDto.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.closeAccount(id);

        verify(accountRepository).findById(id);
        verify(accountRepository).save(account);
        assertEquals(accountDto, result);
        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }
}
