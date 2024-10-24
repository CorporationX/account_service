package faang.school.accountservice.service.account;

import faang.school.accountservice.config.account.AccountProperties;
import faang.school.accountservice.config.generator.AccountNumberGenerator;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.dto.type.TypeDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.IllegalStatusException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.owner.OwnerService;
import faang.school.accountservice.service.status.AccountStatusActions;
import faang.school.accountservice.service.type.TypeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String TEST = "TEST";

    private static final int ID = 1;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountProperties accountProperties;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private OwnerService ownerService;

    @Mock
    private TypeService typeService;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Mock
    private AccountStatusActions accountStatusActions;

    @Mock
    private AccountProperties.NameLength nameLength;

    private Account account;
    private AccountDto accountDto;
    private List<Account> accounts;
    private List<AccountDto> accountDtos;
    private List<AccountStatus> availableAccountStatuses;

    @BeforeEach
    void init() {
        account = Account.builder()
                .build();

        accountDto = AccountDto.builder()
                .build();

        accounts = List.of(account);

        accountDtos = List.of(accountDto);
    }

    @Test
    @DisplayName("Method should return list of accounts")
    void whenCallThenReturnListOfAccounts() {
        when(accountRepository.findAll())
                .thenReturn(accounts);
        when(accountMapper.toAccountDtos(accounts))
                .thenReturn(accountDtos);

        accountService.getAccounts();

        verify(accountRepository).findAll();
        verify(accountMapper).toAccountDtos(accounts);
    }

    @Test
    @DisplayName("When account exists with incoming number then not thrown exception")
    void whenAccountWithNumberExistsThenNotThrownException() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account))
                .thenReturn(accountDto);

        accountService.getAccountByNumber(anyLong());

        verify(accountRepository).findById(anyLong());
        verify(accountMapper).toAccountDto(account);
    }

    @Test
    @DisplayName("When account not exists with incoming number then exception thrown")
    void whenAccountWithNumberNotExistsThenExceptionThrown() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accountService.getAccountByNumber(anyLong()));
    }

    @Test
    @DisplayName("When incoming dto is correct then create account successfully")
    void whenIncomingDtoIsCorrectThenCreateNewAccount() {
        AccountType type = AccountType.builder()
                .id(ID)
                .name(TEST)
                .build();
        Owner owner = Owner.builder()
                .id(ID)
                .name(TEST)
                .build();

        AccountCreateDto accountCreateDto = AccountCreateDto.builder()
                .type(TypeDto.builder()
                        .name(TEST)
                        .build())
                .currency(Currency.RUB)
                .owner(OwnerDto.builder()
                        .name(TEST)
                        .build())
                .build();

        when(typeService.getTypeByName(accountCreateDto.getType().getName()))
                .thenReturn(type);
        when(ownerService.getOwnerByName(accountCreateDto.getOwner().getName()))
                .thenReturn(owner);
        when(accountNumberGenerator.generateRandomAccountNumberInRange())
                .thenReturn(TEST);
        when(accountMapper.toAccountDto(any()))
                .thenReturn(accountDto);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        accountService.createAccount(accountCreateDto);

        verify(typeService)
                .getTypeByName(accountCreateDto.getType().getName());
        verify(ownerService)
                .getOwnerByName(accountCreateDto.getOwner().getName());
        verify(accountRepository)
                .save(any(Account.class));
        verify(accountMapper)
                .toAccountDto(any(Account.class));
    }

    @Test
    @DisplayName("When block account and account exists and status correct then not thrown exception")
    void whenAccountExistsAndStatusCorrectThenNotThrownException() {
        account.setStatus(AccountStatus.ACTIVE);
        availableAccountStatuses = List.of(AccountStatus.FROZEN);

        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);
        when(accountMapper.toAccountDto(account))
                .thenReturn(accountDto);
        when(accountStatusActions.getAvailableActions(account.getStatus()))
                .thenReturn(availableAccountStatuses);

        accountService.blockAccount(anyLong());

        verify(accountRepository)
                .findById(anyLong());
        verify(accountRepository)
                .save(any(Account.class));
        verify(accountMapper)
                .toAccountDto(any(Account.class));
        verify(accountStatusActions)
                .getAvailableActions(any());
    }

    @Test
    @DisplayName("When block account and account exists and status incorrect then thrown exception")
    void whenAccountExistsAndStatusIncorrectThenThrownException() {
        account.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(account));

        assertThrows(IllegalStatusException.class,
                () -> accountService.blockAccount(anyLong()));
    }

    @Test
    @DisplayName("When block account and account not exists then throw exception")
    void whenAccountNotExistsWhileBlockingThenThrowException() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accountService.blockAccount(anyLong()));
    }

    @Test
    @DisplayName("When block account and account exists and status correct then not thrown exception")
    void whenAccountExistsAndStatusCorrectWhileClosingThenNotThrownException() {
        account.setStatus(AccountStatus.ACTIVE);
        availableAccountStatuses = List.of(AccountStatus.CLOSED);

        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);
        when(accountStatusActions.getAvailableActions(account.getStatus()))
                .thenReturn(availableAccountStatuses);

        accountService.closeAccount(anyLong());

        verify(accountRepository)
                .findById(anyLong());
        verify(accountRepository)
                .save(any(Account.class));
        verify(accountStatusActions)
                .getAvailableActions(any());
    }

    @Test
    @DisplayName("When block account and account exists and status incorrect then thrown exception")
    void whenAccountExistsAndStatusIncorrectWhileClosingThenThrownException() {
        account.setStatus(AccountStatus.CLOSED);

        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(account));

        assertThrows(IllegalStatusException.class,
                () -> accountService.closeAccount(anyLong()));
    }

    @Test
    @DisplayName("When close account and account not exists then throw exception")
    void whenAccountNotExistsWhileClosingThenThrowException() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accountService.closeAccount(anyLong()));
    }
}