package faang.school.accountservice.service.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.free.FreeAccountNumbersService;
import faang.school.accountservice.validator.account.AccountValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepo;
    @Mock
    private AccountValidator validator;
    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @Spy
    private AccountMapperImpl mapper;

    @Test
    void testOpenAccount(){
        AccountDto accountDto = new AccountDto();
        Account account = Account.builder().id(1L).type(AccountType.INVESTMENT_ACCOUNT).build();
        when(mapper.toEntity(accountDto)).thenReturn(account);
        String number = "4200_0000_0000_0000";
        when(freeAccountNumbersService.getFreeAccountNumber(AccountType.INVESTMENT_ACCOUNT))
                .thenReturn(number);
        Account expected = Account.builder()
                .id(1L)
                .type(AccountType.INVESTMENT_ACCOUNT)
                .status(AccountStatus.ACTIVE)
                .paymentNumber(number)
                .createdAt(any())
                .build();
        AccountDto expectedDto = AccountDto.builder()
                .type(AccountType.INVESTMENT_ACCOUNT)
                .status(AccountStatus.ACTIVE)
                .paymentNumber(number)
                .build();;
        when(accountRepo.save(expected)).thenReturn(expected);
        when(mapper.toDto(expected)).thenReturn(expectedDto);
        AccountDto result = accountService.openAccount(accountDto);
        verify(accountRepo, times(1)).save(any());
        assertEquals(expectedDto.getType(), result.getType());
        assertEquals(expectedDto.getStatus(), result.getStatus());
        assertEquals(expectedDto.getPaymentNumber(), result.getPaymentNumber());
    }

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
