package faang.school.accountservice.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.controller.AccountController;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.enums.AccountStatus;
import faang.school.accountservice.entity.account.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.account.AccountException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.validator.account.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
        doThrow(AccountException.class).when(accountRepo).findById(id);

        assertThrows(AccountException.class, () -> accountService.get(id));
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
        assertNotNull(result.getClosedAt());
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
        assertNotNull(result.getClosedAt());
    }
}
