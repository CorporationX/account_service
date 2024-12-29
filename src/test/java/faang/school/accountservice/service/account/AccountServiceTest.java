package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    private final long USER_ID = 12;
    private final long ACCOUNT_ID = 17;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Spy
    private AccountMapperImpl accountMapper;
    @Mock
    private AccountValidator validator;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountType(AccountType.FL)
                .currency(Currency.RUB)
                .status(AccountStatus.OPEN)
                .build();
    }

    @Test
    void testGetAccountWithNumberSuccess() {
        account.setStatus(AccountStatus.CLOSE);
        when(validator.validateAccount("40817810099910004312")).thenReturn(account);
        ResponseAccountDto responseAccountDto = accountService
                .getAccountWithNumber("40817810099910004312", USER_ID);
        assertEquals(AccountStatus.CLOSE, responseAccountDto.getStatus());
    }

    @Test
    void testGetAccountWithIdSuccess() {
        account.setStatus(AccountStatus.FREEZE);
        when(validator.validateAccount(ACCOUNT_ID)).thenReturn(account);
        ResponseAccountDto responseAccountDto = accountService
                .getAccountWithId(ACCOUNT_ID, USER_ID);
        assertEquals(AccountStatus.FREEZE, responseAccountDto.getStatus());
    }

    @Test
    void testBlockAccountSuccess() {
        when(validator.validateAccount(ACCOUNT_ID)).thenReturn(account);
        accountService.blockAccount(ACCOUNT_ID, USER_ID);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(AccountStatus.FREEZE, accountCaptor.getValue().getStatus());

    }

    @Test
    void testUnblockAccountSuccess() {
        when(validator.validateAccount(ACCOUNT_ID)).thenReturn(account);
        accountService.unblockAccount(ACCOUNT_ID, USER_ID);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(AccountStatus.OPEN, accountCaptor.getValue().getStatus());
    }

    @Test
    void testCloseAccountSuccess() {
        when(validator.validateAccount(ACCOUNT_ID)).thenReturn(account);
        accountService.closeAccount(ACCOUNT_ID, USER_ID);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(AccountStatus.CLOSE, accountCaptor.getValue().getStatus());
    }


}
