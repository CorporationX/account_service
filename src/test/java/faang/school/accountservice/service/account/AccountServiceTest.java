package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepo;
    @Mock
    private AccountValidator validator;

    @Spy
    private AccountMapperImpl mapper;

    @Test
    public void testGetWithException() {
        Long id = 1L;
        doThrow(AccountNotFoundException.class).when(accountRepo).findById(id);

        assertThrows(AccountNotFoundException.class, () -> accountService.get(id));
    }

    @Test
    public void testFreezingAccount() {
        Long id = 1L;
        Account account = Account
                .builder()
                .id(id)
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepo.findById(id)).thenReturn(Optional.of(account));
        when(accountRepo.save(account)).thenReturn(account);

        AccountDto result = accountService.freezeAccount(id);

        assertEquals(AccountStatus.FROZEN, result.getStatus());
    }

    @Test
    public void testClosingAccount() {
        Long id = 1L;
        Account account = Account
                .builder()
                .id(id)
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepo.findById(id)).thenReturn(Optional.of(account));
        when(accountRepo.save(account)).thenReturn(account);

        AccountDto result = accountService.closeAccount(id);

        assertEquals(AccountStatus.CLOSED, result.getStatus());
    }
}
