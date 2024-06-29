package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Consumer;

import static faang.school.accountservice.enums.AccountType.RUBLE_ACCOUNT_FOR_INDIVIDUALS;
import static faang.school.accountservice.enums.Currency.RUB;
import static faang.school.accountservice.enums.Status.ACTIVE;
import static faang.school.accountservice.enums.Status.CLOSED;
import static faang.school.accountservice.enums.Status.FROZEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    private Account accountFromRepo;
    private long accountId;
    private long number;
    private AccountDto accountDto;
    private FreeAccountNumber freeAccountNumber;

    @BeforeEach
    void setUp() {
        accountFromRepo = Account.builder()
                .id(3L)
                .number("1324123413241234")
                .ownerId(10L)
                .type(RUBLE_ACCOUNT_FOR_INDIVIDUALS)
                .currency(RUB)
                .status(ACTIVE)
                .build();

        accountDto = AccountDto.builder()
                .ownerId(3L)
                .type(RUBLE_ACCOUNT_FOR_INDIVIDUALS)
                .currency(RUB)
                .build();

        accountId = 3L;
        freeAccountNumber = FreeAccountNumber.builder()
                .account_number(123456789L)
                .type(RUBLE_ACCOUNT_FOR_INDIVIDUALS)
                .build();
    }

    @Test
    void testGetAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountFromRepo));
        AccountDto expected = accountMapper.toDto(accountFromRepo);
        AccountDto actual = accountService.getAccount(accountId);
        assertEquals(expected, actual);
    }

    @Test
    void testOpenAccount() {
        AccountDto accountDto = new AccountDto();
        Account account = new Account();
        account.setType(RUBLE_ACCOUNT_FOR_INDIVIDUALS);
        Account savedAccount = new Account();
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber();
        freeAccountNumber.setAccount_number(123456789L);
        freeAccountNumber.setType(RUBLE_ACCOUNT_FOR_INDIVIDUALS);

        doNothing().when(accountValidator).initValidation(accountDto);
        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        doAnswer(invocation -> {
            Consumer<FreeAccountNumber> consumer = invocation.getArgument(1);
            consumer.accept(freeAccountNumber);
            return null;
        }).when(freeAccountNumbersService).getAndHandleAccountNumber(eq(account.getType()), any());
        when(accountRepository.save(account)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(accountDto);

        AccountDto result = accountService.openAccount(accountDto);

        verify(accountValidator).initValidation(accountDto);
        verify(accountMapper).toEntity(accountDto);
        verify(freeAccountNumbersService).getAndHandleAccountNumber(eq(account.getType()), any());
        verify(accountRepository).save(account);
        verify(accountMapper).toDto(savedAccount);

        assertEquals(accountDto, result);
        assertEquals("123456789", account.getNumber());
        assertEquals(Status.ACTIVE, account.getStatus());
    }

    @Test
    void testBlockAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountFromRepo));
        accountService.blockAccount(accountId);
        assertEquals(FROZEN, accountFromRepo.getStatus());
    }

    @Test
    void testCloseAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountFromRepo));
        accountService.closeAccount(accountId);
        assertEquals(CLOSED, accountFromRepo.getStatus());
    }

    @Test
    void testActivateAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountFromRepo));
        accountService.activateAccount(accountId);
        assertEquals(ACTIVE, accountFromRepo.getStatus());
    }
}
