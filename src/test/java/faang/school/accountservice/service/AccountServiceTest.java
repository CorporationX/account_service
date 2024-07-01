package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.jpa.AccountJpaRepository;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String ACCOUNT_NUMBER = "123456789123456";
    private static final Balance ACCOUNT_BALANCE = Balance.builder().actualBalance(BigDecimal.valueOf(1000)).build();
    private static final Long ACCOUNT_ID = 1L;
    private static final LocalDateTime ACCOUNT_CREATED_AT = LocalDateTime.of(2024, 6, 20, 18, 25, 25);
    private static final LocalDateTime ACCOUNT_CLOSED_AT = LocalDateTime.of(2024, 7, 20, 18, 25, 25);
    private static final Balance ACCOUNT_DEPOSIT_BALANCE = Balance.builder().actualBalance(BigDecimal.valueOf(1500)).build();
    private static final Balance ACCOUNT_WRITEOFF_BALANCE = Balance.builder().actualBalance(BigDecimal.valueOf(500)).build();

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountJpaRepository accountJpaRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountValidator accountValidator;
    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @InjectMocks
    private AccountService accountService;

    AccountDto newAccountDto;
    AccountDto savedAccountDto;
    Account newAccount;
    Account savedAccount;

    @BeforeEach
    public void init() {
        newAccountDto = AccountDto.builder()
                .number(ACCOUNT_NUMBER)
                .type(AccountType.CURRENT_INDIVIDUALS)
                .currency(Currency.USD)
                .status(AccountStatus.CURRENT)
                .balance(ACCOUNT_BALANCE)
                .build();
        savedAccountDto = AccountDto.builder()
                .id(ACCOUNT_ID)
                .number(ACCOUNT_NUMBER)
                .type(AccountType.CURRENT_INDIVIDUALS)
                .currency(Currency.USD)
                .status(AccountStatus.CURRENT)
                .balance(ACCOUNT_BALANCE)
                .createdAt(ACCOUNT_CREATED_AT)
                .build();
        newAccount = Account.builder()
                .type(AccountType.CURRENT_INDIVIDUALS)
                .currency(Currency.USD)
                .status(AccountStatus.CURRENT)
                .balance(ACCOUNT_BALANCE)
                .createdAt(ACCOUNT_CREATED_AT)
                .build();
        savedAccount = Account.builder()
                .id(ACCOUNT_ID)
                .type(AccountType.CURRENT_INDIVIDUALS)
                .currency(Currency.USD)
                .status(AccountStatus.CURRENT)
                .balance(ACCOUNT_BALANCE)
                .createdAt(ACCOUNT_CREATED_AT)
                .build();

    }

    @Test
    @DisplayName("Find Account when it exists")
    public void findAccountByNumberWhenOk() {
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(savedAccountDto);

        AccountDto actualResult = accountService.findByAccountNumber(ACCOUNT_NUMBER);

        assertEquals(savedAccountDto, actualResult);
    }

    @Test
    @DisplayName("Find Account when it not found")
    public void findAccountByNumberWhenNotFound() {
        AccountDto actualResult = accountService.findByAccountNumber(ACCOUNT_NUMBER);

        assertNull(actualResult);
    }

    @Test
    @DisplayName("Open account when everything ok")
    public void openAccountWhenOk() {
        when(accountMapper.toEntity(newAccountDto)).thenReturn(newAccount);
        when(accountJpaRepository.save(newAccount)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(savedAccountDto);

        AccountDto actualResult = accountService.openAccount(newAccountDto);

        assertEquals(savedAccountDto, actualResult);
    }

    @Test
    @DisplayName("Freeze account when it exists")
    public void freezeAccountWhenOk() {
        savedAccountDto.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(savedAccountDto);

        AccountDto actualResult = accountService.freezeAccount(ACCOUNT_NUMBER);

        assertEquals(savedAccountDto, actualResult);
    }

    @Test
    @DisplayName("Close account when it exists")
    public void closeAccountWhenOk() {
        savedAccountDto.setStatus(AccountStatus.FROZEN);
        savedAccountDto.setClosedAt(ACCOUNT_CLOSED_AT);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(savedAccountDto);

        AccountDto actualResult = accountService.closeAccount(ACCOUNT_NUMBER);

        assertEquals(savedAccountDto, actualResult);
    }

    @Test
    @DisplayName("Deposit account when OK")
    public void depositAccountWhenOk() {
        newAccount.setId(ACCOUNT_ID);
        savedAccount.setBalance(ACCOUNT_DEPOSIT_BALANCE);
        savedAccountDto.setBalance(ACCOUNT_DEPOSIT_BALANCE);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(newAccount);
        when(accountJpaRepository.save(newAccount)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(savedAccountDto);

        AccountDto actualResult = accountService.deposit(ACCOUNT_NUMBER, BigDecimal.valueOf(500));

        assertEquals(savedAccountDto, actualResult);
    }

    @Test
    @DisplayName("WriteOff account when OK")
    public void writeOffAccountWhenOk() {
        newAccount.setId(ACCOUNT_ID);
        savedAccount.setBalance(ACCOUNT_WRITEOFF_BALANCE);
        savedAccountDto.setBalance(ACCOUNT_WRITEOFF_BALANCE);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(newAccount);
        when(accountJpaRepository.save(newAccount)).thenReturn(savedAccount);
        when(accountMapper.toDto(newAccount)).thenReturn(savedAccountDto);

        AccountDto actualResult = accountService.writeOff(ACCOUNT_NUMBER, BigDecimal.valueOf(500));

        assertEquals(savedAccountDto, actualResult);
    }
}