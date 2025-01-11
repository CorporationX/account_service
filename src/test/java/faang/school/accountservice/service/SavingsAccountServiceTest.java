package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.mappers.SavingsAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsAccountServiceTest {
    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SavingsAccountMapper mapper;

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @Test
    void testGetSavingsAccountByIdSuccess() {
        Long savingsAccountId = 1L;
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);

        SavingsAccountDto savingsAccountDto = new SavingsAccountDto();
        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.of(savingsAccount));
        when(mapper.toDto(savingsAccount)).thenReturn(savingsAccountDto);

        SavingsAccountDto result = savingsAccountService.getSavingsAccountById(savingsAccountId);

        assertNotNull(result);
        verify(savingsAccountRepository, times(1)).findById(savingsAccountId);
        verify(mapper, times(1)).toDto(savingsAccount);
    }

    @Test
    void testGetSavingsAccountByIdNotFound() {
        Long savingsAccountId = 1L;
        when(savingsAccountRepository.findById(savingsAccountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> savingsAccountService.getSavingsAccountById(savingsAccountId));
        assertEquals("Savings account with id " + savingsAccountId + " not found", exception.getMessage());
        verify(savingsAccountRepository, times(1)).findById(savingsAccountId);
    }

    @Test
    void testCreateAccountInvalidAccountType() {
        Long accountId = 1L;
        SavingsAccountDto savingsAccountDto = new SavingsAccountDto();
        savingsAccountDto.setId(accountId);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountType(AccountType.BUSINESS);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> savingsAccountService.createAccount(savingsAccountDto));
        assertEquals("ERROR: Account type is not SAVINGS!", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testValidateSavingsAccountIdInvalidId() {
        Long invalidId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> savingsAccountService.getSavingsAccountById(invalidId));
        assertEquals("ERROR: ID cannot be NULL or LESS THAN 0", exception.getMessage());
    }
}
