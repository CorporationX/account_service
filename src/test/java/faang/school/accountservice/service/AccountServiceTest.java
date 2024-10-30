package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.client.UserDto;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.AccountStatus;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.model.owner.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AccountService accountService;

    @Mock
    BalanceService balanceService;

    private Account account;
    private final String accountNumber = "00000000000000000000";

    @BeforeEach
    void setUp() {
        Owner owner = Owner.builder()
                .externalId(1L)
                .type(OwnerType.USER)
                .build();

        Balance balance = Balance.builder()
                .authorization(0.0)
                .actual(100.0)
                .build();

        account = Account.builder()
                .owner(owner)
                .balance(balance)
                .accountNumber(accountNumber)
                .build();
    }

    @Test
    void createAccount_shouldCreateNewOwner() {
        when(ownerRepository.findOwner(anyLong(), any())).thenReturn(Collections.emptyList());
        when(userServiceClient.getUser(anyLong())).thenReturn(UserDto.builder().id(1L).build());

        accountService.createAccount(account);

        verify(ownerRepository).save(any(Owner.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_shouldUseExistingOwner() {
        Owner existingOwner = Owner.builder()
                .externalId(1L)
                .type(OwnerType.USER)
                .build();

        when(ownerRepository.findOwner(anyLong(), any())).thenReturn(List.of(existingOwner));

        accountService.createAccount(account);

        verify(ownerRepository, never()).save(any(Owner.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccountByNumber_shouldReturnAccount() {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.getAccountByNumber(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void suspendAccount_shouldSuspendAccount() {
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.suspendAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void suspendAccount_shouldThrowExceptionForNonActiveAccount() {
        account.setStatus(AccountStatus.SUSPENDED);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountService.suspendAccount(accountNumber));
    }

    @Test
    void activateAccount_shouldActivateAccount() {
        account.setStatus(AccountStatus.SUSPENDED);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.activateAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void closeAccount_shouldCloseAccount() {
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.closeAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void closeAccount_shouldThrowExceptionForAlreadyClosedAccount() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountService.closeAccount(accountNumber));
    }

    @Test
    void validateAccountNumber_shouldThrowExceptionForInvalidNumber() {
        String invalidAccountNumber = "00000000000aaa000000";

        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByNumber(invalidAccountNumber));
    }
}