package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private static final long ACCOUNT_ID = 1L;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountService accountService;

    private AccountDto accountDto;
    private Account account;

    @BeforeEach
    void setUp() {
        accountDto = AccountDto.builder()
                .id(ACCOUNT_ID)
                .build();
        account = Account.builder()
                .id(ACCOUNT_ID)
                .build();
    }

    @Test
    public void whenOpenThenAccountDto() {
        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountDto);
        AccountDto actual = accountService.open(accountDto);
        assertThat(actual).isEqualTo(accountDto);
    }

    @Test
    public void whenGetAndAccountExistThenAccountDto() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(accountDto);
        AccountDto actual = accountService.get(ACCOUNT_ID);
        assertThat(actual).isEqualTo(accountDto);
    }

    @Test
    public void whenGetAndAccountNotExistThenException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class,
                () -> accountService.get(ACCOUNT_ID));
    }

    @Test
    public void whenBlockAndAccountNotExistThenException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class,
                () -> accountService.block(ACCOUNT_ID));
    }

    @Test
    public void whenBlockSuccessfully() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        accountService.block(ACCOUNT_ID);
        verify(accountRepository).save(account);
    }

    @Test
    public void whenCloseAndAccountNotExistThenException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class,
                () -> accountService.close(ACCOUNT_ID));
    }

    @Test
    public void whenCloseSuccessfully() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        accountService.close(ACCOUNT_ID);
        verify(accountRepository).save(account);
    }
}