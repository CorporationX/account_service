package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.entity.account.Type;
import faang.school.accountservice.mapper.account.AccountMapperImpl;
import faang.school.accountservice.mapper.account.CreateAccountMapperImpl;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.validator.account.AccountServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;
    @Spy
    private AccountMapperImpl accountMapper;
    @Spy
    private CreateAccountMapperImpl createAccountMapper;
    @Mock
    private AccountServiceValidator validator;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private Account firstAccount;

    private Account secondAccount;


    @BeforeEach
    void setUp() {
        firstAccount = new Account();
        secondAccount = new Account();
    }

    @Test
    void testValidatorThrownAnExceptionWithInvalidId() {
        Mockito.doThrow(IllegalArgumentException.class).when(validator).checkId(-1, -1);

        assertThrows(IllegalArgumentException.class,
                () -> accountService.getAccount(-1, -1));
    }

    @Test
    void testGetAccountCorrectWork() {
        firstAccount.setId(1);
        secondAccount.setId(2);

        when(accountRepository.findByOwnerIdAndOwnerType(1, OwnerType.USER))
                .thenReturn(List.of(firstAccount, secondAccount));

        List<AccountDto> accountDtos = accountService.getAccount(1, 0);

        assertEquals(accountDtos.size(), 2);
        assertEquals(accountDtos.get(0).getId(), firstAccount.getId());
        assertEquals(accountDtos.get(1).getId(), secondAccount.getId());
    }

    @Test
    void testOpenNewAccountCorrectWork() {
        CreateAccountDto createAccountDto = new CreateAccountDto();
        createAccountDto.setType(Type.FOREX_ACCOUNT);
        createAccountDto.setOwnerType(OwnerType.USER);
        createAccountDto.setOwnerId(1);
        createAccountDto.setCurrency(Currency.EUR);

        AccountDto accountDto = accountService.openNewAccount(createAccountDto);

        verify(accountRepository).save(accountCaptor.capture());
        verify(validator).checkId(createAccountDto.getOwnerId());
        verify(validator).validateCreateAccountDto(createAccountDto);

        assertEquals(accountCaptor.getValue().getStatus(), Status.ACTIVE);

        assertEquals(accountDto.getStatus(), Status.ACTIVE);
        assertEquals(accountDto.getType(), Type.FOREX_ACCOUNT);
        assertEquals(accountDto.getOwnerType(), OwnerType.USER);
        assertEquals(accountDto.getCurrency(), Currency.EUR);
    }

    @Test
    void testChangeStatusWhenArgumentIsClosed() {
        long accountId = 1L;
        Status newStatus = Status.CLOSED;

        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.changeStatus(accountId, newStatus);

        verify(validator, times(1)).checkId(accountId);
        assertEquals(newStatus, account.getStatus());
        assertNotNull(account.getClosedAt());
        verify(accountRepository).save(account);
    }

    @Test
    void testAccountRepositoryThrownAnException(){
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                ()->accountService.changeStatus(1,Status.CLOSED));

        verify(accountRepository,never()).save(any());
    }
}