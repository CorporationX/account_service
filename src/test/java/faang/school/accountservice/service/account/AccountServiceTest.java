package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.account_owner.AccountOwnerDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.IllegalAccountStatus;
import faang.school.accountservice.exception.IllegalAccountTypeForOwner;
import faang.school.accountservice.exception.NotUniqueAccountNumberException;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.filter.account.AccountStatusFilter;
import faang.school.accountservice.filter.account.AccountTypeFilter;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import faang.school.accountservice.validation.AccountOwnerValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private final static long OWNER_ID = 1L;
    private final static long ACCOUNT_ID = 1L;
    private final static long CLOSED_ACCOUNT_ID = 2L;
    private final static Pageable PAGEABLE = PageRequest.of(0, 2);
    private final List<Filter<AccountFilterDto, Account>> filters = new ArrayList<>();
    private Account account;
    private Account closedAccount;
    private AccountOwner owner;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountOwnerService accountOwnerService;

    @Mock
    private AccountOwnerValidator accountOwnerValidator;

    @Spy
    AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    public void setUp() {
        Filter<AccountFilterDto, Account> typeFilter = mock(AccountTypeFilter.class);
        Filter<AccountFilterDto, Account> statusFilter = mock(AccountStatusFilter.class);
        filters.add(typeFilter);
        filters.add(statusFilter);

        account = Account.builder()
                .id(ACCOUNT_ID)
                .status(AccountStatus.ACTIVE)
                .build();

        closedAccount = Account.builder()
                .id(CLOSED_ACCOUNT_ID)
                .status(AccountStatus.CLOSED)
                .closedAt(LocalDateTime.of(2024, 10, 26, 5, 15))
                .build();

        owner = AccountOwner.builder()
                .id(1L)
                .ownerId(OWNER_ID)
                .ownerType(OwnerType.USER)
                .build();

        accountService = new AccountServiceImpl(accountRepository, accountMapper, accountOwnerService, accountOwnerValidator, filters);
    }

    @Test
    public void testGetAllProjectsWithoutFilters() {
        when(accountRepository.findAll(Mockito.<Specification<Account>>any(), any(Pageable.class))).thenReturn(preparePageOfAccounts());

        Page<AccountDto> result = accountService.getAllAccounts(null, OWNER_ID, PAGEABLE.getPageNumber(), PAGEABLE.getPageSize());

        assertEquals(2, result.getTotalElements());
        verify(accountRepository).findAll(Mockito.<Specification<Account>>any(), any(Pageable.class));
        verify(accountOwnerValidator).validateOwnerByAccountOwnerId(OWNER_ID);
    }

    @Test
    public void testGetAllProjectsWithFilters() {
        AccountFilterDto filterDto = new AccountFilterDto(AccountType.INDIVIDUAL_ACCOUNT, AccountStatus.ACTIVE, Currency.EUR);
        when(accountRepository.findAll(Mockito.<Specification<Account>>any(), any(Pageable.class))).thenReturn(preparePageOfAccounts());
        when(filters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(0).apply(filterDto)).thenReturn(any());
        when(filters.get(1).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(1).apply(filterDto)).thenReturn(any());

        Page<AccountDto> result = accountService.getAllAccounts(filterDto, OWNER_ID, PAGEABLE.getPageNumber(), PAGEABLE.getPageSize());

        assertEquals(2, result.getTotalElements());
        verify(accountRepository).findAll(Mockito.<Specification<Account>>any(), any(Pageable.class));
        verify(filters.get(0)).isApplicable(filterDto);
        verify(filters.get(1)).isApplicable(filterDto);
        verify(filters.get(0)).apply(filterDto);
        verify(filters.get(1)).apply(filterDto);
        verify(accountOwnerValidator).validateOwnerByAccountOwnerId(OWNER_ID);
    }

    @Test
    public void testGetAccountById() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccount(ACCOUNT_ID);

        assertEquals(1L, result.id());
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountOwnerValidator).validateOwner(account.getOwner());
    }

    @Test
    public void testGetNonExistingAccount() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.getAccount(2L));
        verify(accountRepository).findById(2L);
    }

    @Test
    public void testCreateAccount() {
        CreateAccountDto createAccountDto = new CreateAccountDto("1234567891012", new AccountOwnerDto(owner.getOwnerId(), owner.getOwnerType()), Currency.RUB, AccountType.INDIVIDUAL_ACCOUNT);
        when(accountOwnerService.getOrCreateAccountOwner(owner.getOwnerId(), owner.getOwnerType())).thenReturn(owner);
        when(accountRepository.existsAccountByAccountNumber(createAccountDto.accountNumber())).thenReturn(false);

        Account accountToSave = accountMapper.toAccount(createAccountDto);
        accountToSave.setStatus(AccountStatus.ACTIVE);
        accountToSave.setOwner(owner);
        accountService.createAccount(createAccountDto);

        verify(accountOwnerService).getOrCreateAccountOwner(owner.getOwnerId(), owner.getOwnerType());
        verify(accountRepository).existsAccountByAccountNumber(createAccountDto.accountNumber());
        verify(accountRepository).save(accountToSave);
        verify(accountOwnerValidator).validateOwnerByOwnerIdAndType(owner.getOwnerId(), owner.getOwnerType());
    }

    @Test
    public void testCreateAccountWithExistingNumber() {
        CreateAccountDto createAccountDto = new CreateAccountDto("1234567891012", new AccountOwnerDto(owner.getOwnerId(), owner.getOwnerType()), Currency.RUB, AccountType.INDIVIDUAL_ACCOUNT);
        when(accountOwnerService.getOrCreateAccountOwner(owner.getOwnerId(), owner.getOwnerType())).thenReturn(owner);
        when(accountRepository.existsAccountByAccountNumber(createAccountDto.accountNumber())).thenReturn(true);

        assertThrows(NotUniqueAccountNumberException.class, () -> accountService.createAccount(createAccountDto));

        verify(accountOwnerService).getOrCreateAccountOwner(owner.getOwnerId(), owner.getOwnerType());
        verify(accountRepository).existsAccountByAccountNumber(createAccountDto.accountNumber());
        verify(accountOwnerValidator).validateOwnerByOwnerIdAndType(owner.getOwnerId(), owner.getOwnerType());
    }

    @Test
    public void testCreateAccountWithNotAllowedType() {
        CreateAccountDto createAccountDto = new CreateAccountDto("1234567891012", new AccountOwnerDto(owner.getOwnerId(), owner.getOwnerType()), Currency.RUB, AccountType.LEGAL_ENTITY_ACCOUNT);
        when(accountOwnerService.getOrCreateAccountOwner(owner.getOwnerId(), owner.getOwnerType())).thenReturn(owner);

        assertThrows(IllegalAccountTypeForOwner.class, () -> accountService.createAccount(createAccountDto));

        verify(accountOwnerService).getOrCreateAccountOwner(owner.getOwnerId(), owner.getOwnerType());
    }

    @Test
    public void testUpdateAccountStatus() {
        UpdateAccountDto updateAccountDto = new UpdateAccountDto(AccountStatus.ACTIVE, null, null);
        when(accountRepository.findById(CLOSED_ACCOUNT_ID)).thenReturn(Optional.of(closedAccount));

        assertEquals(AccountStatus.CLOSED, closedAccount.getStatus());
        assertNotNull(closedAccount.getClosedAt());

        accountService.updateAccount(CLOSED_ACCOUNT_ID, updateAccountDto);

        assertEquals(AccountStatus.ACTIVE, closedAccount.getStatus());
        assertNull(closedAccount.getClosedAt());

        verify(accountRepository).findById(CLOSED_ACCOUNT_ID);
        verify(accountRepository).save(closedAccount);
        verify(accountOwnerValidator).validateOwner(closedAccount.getOwner());
    }

    @Test
    public void testUpdateAccountWithSameStatus() {
        UpdateAccountDto updateAccountDto = new UpdateAccountDto(AccountStatus.ACTIVE, null, null);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        assertThrows(IllegalAccountStatus.class, () -> accountService.updateAccount(ACCOUNT_ID, updateAccountDto));
    }

    private Page<Account> preparePageOfAccounts() {
        List<Account> accounts = prepareAccounts();
        return new PageImpl<>(accounts, PAGEABLE, accounts.size());
    }

    private List<Account> prepareAccounts() {
        List<Account> accounts = new ArrayList<>();
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.INDIVIDUAL_ACCOUNT)
                .accountNumber("1234567891012")
                .owner(owner)
                .status(AccountStatus.ACTIVE)
                .currency(Currency.RUB)
                .build();
        Account account1 = Account.builder()
                .id(2L)
                .accountType(AccountType.INDIVIDUAL_ACCOUNT)
                .accountNumber("12345678910124")
                .status(AccountStatus.FROZEN)
                .owner(owner)
                .currency(Currency.EUR)
                .build();

        accounts.add(account);
        accounts.add(account1);
        return accounts;
    }
}
