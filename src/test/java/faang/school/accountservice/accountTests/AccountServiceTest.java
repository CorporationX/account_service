package faang.school.accountservice.accountTests;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository repository;
    @Spy
    private AccountMapperImpl mapper;

    @InjectMocks
    private AccountService service;

    @Test
    public void getAccountTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Account()));
        assertNotNull(service.getAccountDto(1L));
    }

    @Test
    public void getNonexistentIdTest() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,() -> service.getAccountDto(1L));
    }

    @Test
    public void openAccountTest() {
        AccountDto dto = new AccountDto(1L, null, OwnerType.USER, 1, AccountType.DEPOSIT, Currency.USD);
        Account account = mapper.toEntity(dto);
        account.setStatus(Status.ACTIVE);
        when(repository.save(any())).thenReturn(account);
        assertEquals(dto, service.openAccount(dto));
    }

    @Test
    public void blockAccountTest() {
        Account newAcc = new Account();
        when(repository.findById(1L)).thenReturn(Optional.of(newAcc));
        newAcc.setStatus(Status.FROZEN);
        when(repository.save(newAcc)).thenReturn(newAcc);
        assertNotNull(service.blockAccount(1L));
    }

    @Test
    public void closeAccountTest() {
        Account newAcc = new Account();
        when(repository.findById(1L)).thenReturn(Optional.of(newAcc));
        newAcc.setStatus(Status.CLOSED);
        when(repository.save(newAcc)).thenReturn(newAcc);
        assertNotNull(service.closeAccount(1L));
    }
}
