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

    private String accountNumber;
    private AccountNumberPool accountNumberPool;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountNumber = "1234567890";

        accountNumberPool = new AccountNumberPool();
        accountNumberPool.setAccountNumber(accountNumber);
        accountNumberPool.setId(UUID.randomUUID());
    }

    @Test
    void testGetAccountNumber_FromPool() {
        when(accountNumberPoolRepository.findTopByOrderByIdAsc()).thenReturn(accountNumberPool);

        String resultAccountNumber = accountNumberService.getAccountNumber();

        assertEquals(accountNumber, resultAccountNumber);
        verify(accountNumberPoolRepository, times(1)).deleteByAccountNumber("1234567890");
    }

    @Test
    void testGetAccountNumber_WhenPoolIsEmpty() {
        when(accountNumberPoolRepository.findTopByOrderByIdAsc()).thenReturn(null).thenReturn(accountNumberPool);

        String resultAccountNumber = accountNumberService.getAccountNumber();

        assertEquals(accountNumber, resultAccountNumber);
        verify(accountNumberPoolRepository, times(2)).findTopByOrderByIdAsc();
        verify(accountNumberPoolRepository, times(1)).deleteByAccountNumber(accountNumber);
        verify(accountNumberPoolRepository, times(10)).save(any(AccountNumberPool.class));  // 10 вызовов на сохранение новых номеров
    }



    @Test
    void testPopulateAccountNumbers() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(accountNumber);

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