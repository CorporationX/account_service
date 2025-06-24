package faang.school.accountservice.accountTests;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exceptions.InsufficientFundsException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
        assertNotNull(service.getAccount(1L));
    }

    @Test
    public void getNonexistentIdTest() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,() -> service.getAccount(1L));
    }

    @Test
    public void openAccountTest() {
        AccountDto dto = new AccountDto(1L, Account.Owner.USER, 1, Account.Type.DEPOSIT, Currency.USD, "1");
        Account account = mapper.toEntity(dto);
        account.setStatus(Account.Status.ACTIVE);
        when(repository.save(account)).thenReturn(account);
        assertEquals(dto, service.openAccount(dto));
    }

    @Test
    public void blockAccountTest() {
        Account newAcc = new Account();
        when(repository.findById(1L)).thenReturn(Optional.of(newAcc));
        newAcc.setStatus(Account.Status.FROZEN);
        when(repository.save(newAcc)).thenReturn(newAcc);
        assertNotNull(service.blockAccount(1L));
    }

    @Test
    public void closeAccountTest() {
        Account newAcc = new Account();
        when(repository.findById(1L)).thenReturn(Optional.of(newAcc));
        newAcc.setStatus(Account.Status.CLOSED);
        when(repository.save(newAcc)).thenReturn(newAcc);
        assertNotNull(service.closeAccount(1L));
    }

    @Test
    public void addBalanceTest() {
        Account acc = Account.builder().balance(BigDecimal.valueOf(0.0)).build();
        when(repository.findById(1L)).thenReturn(Optional.of(acc));
        service.addBalance(1L, BigDecimal.ONE);
        acc.setBalance(BigDecimal.valueOf(2));
        verify(repository, times(1)).save(acc);
    }

    @Test
    public void spendBalanceTest() {
        Account acc = Account.builder().balance(BigDecimal.ONE).build();
        when(repository.findById(1L)).thenReturn(Optional.of(acc));
        service.spendBalance(1L, BigDecimal.ONE);
        acc.setBalance(BigDecimal.ZERO);
        verify(repository, times(1)).save(acc);
    }

    @Test
    public void negativeBalanceTest() {
        Account acc = Account.builder().balance(BigDecimal.ONE).build();
        when(repository.findById(1L)).thenReturn(Optional.of(acc));
        acc.setBalance(BigDecimal.ZERO);
        assertThrows(InsufficientFundsException.class, () -> service.spendBalance(1L, BigDecimal.TEN));
    }
}
