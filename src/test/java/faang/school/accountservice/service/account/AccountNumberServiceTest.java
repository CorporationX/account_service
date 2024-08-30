package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.AccountNumberPool;
import faang.school.accountservice.repository.account.AccountNumberPoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountNumberServiceTest {

    @Mock
    private AccountNumberPoolRepository accountNumberPoolRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountNumberService accountNumberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccountNumber_FromPool() {
        AccountNumberPool accountNumberPool = new AccountNumberPool();
        accountNumberPool.setAccountNumber("1234567890");
        accountNumberPool.setId(UUID.randomUUID());

        when(accountNumberPoolRepository.findTopByOrderByIdAsc()).thenReturn(accountNumberPool);

        String accountNumber = accountNumberService.getAccountNumber();

        assertEquals("1234567890", accountNumber);
        verify(accountNumberPoolRepository, times(1)).deleteByAccountNumber("1234567890");
    }

    @Test
    void testGetAccountNumber_WhenPoolIsEmpty() {
        AccountNumberPool accountNumberPool = new AccountNumberPool();
        accountNumberPool.setAccountNumber("1234567890");
        accountNumberPool.setId(UUID.randomUUID());

        when(accountNumberPoolRepository.findTopByOrderByIdAsc()).thenReturn(null).thenReturn(accountNumberPool);

        String accountNumber = accountNumberService.getAccountNumber();

        assertEquals("1234567890", accountNumber);
        verify(accountNumberPoolRepository, times(2)).findTopByOrderByIdAsc();
        verify(accountNumberPoolRepository, times(1)).deleteByAccountNumber("1234567890");
        verify(accountNumberPoolRepository, times(10)).save(any(AccountNumberPool.class));  // 10 вызовов на сохранение новых номеров
    }



    @Test
    void testPopulateAccountNumbers() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn("1234567890");

        accountNumberService.populateAccountNumbers(1);

        verify(accountNumberGenerator, times(1)).generateAccountNumber();
        verify(accountNumberPoolRepository, times(1)).save(any(AccountNumberPool.class));
    }

    @Test
    void testGetAccountNumberPoolSize() {
        when(accountNumberPoolRepository.count()).thenReturn(5L);

        int poolSize = accountNumberService.getAccountNumberPoolSize();

        assertEquals(5, poolSize);
    }
}