package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSequence;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.repository.AccountSequenseRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.entity.AccountType.DEBIT;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumbersServiceUnitTests {
    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;
    @Mock
    private AccountSequenseRepository accountSequenseRepository;
    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    private int butchSize = 10;
    private AccountType type = DEBIT;
    private AccountSequence accountSequence = new AccountSequence();
    private boolean consumerIsCalled = false;

    @BeforeEach
    void setUp() {
        accountSequence.setCounter(butchSize);
        accountSequence.setInitialCounter(0);
        accountSequence.setType(type);
        consumerIsCalled = false;
    }

    // write test cases here
    @Test
    public void testGenerateAccountNumber() {


        when(accountSequenseRepository.incrementCounter(anyString(), anyInt())).thenReturn(accountSequence);
        freeAccountNumbersService.generateAccountNumbers(type, butchSize);
        verify(freeAccountNumbersRepository, times(1)).saveAll(anyList());

    }

    @Test
    public void testGenerateAccountNumbersNullTypeException() {
        assertThrows(NullPointerException.class, () -> {
            freeAccountNumbersService.generateAccountNumbers(null, 10);
        });
    }

    @Test
    public void testGenerateAccountNumbersIncrementCounterException() {
        assertThrows(NullPointerException.class, () -> {
            freeAccountNumbersService.generateAccountNumbers(DEBIT, 1);
        });
    }

    @Test
    public void testGenerateAccountNumbersNullButchSizeException() {
        int butchSize = 0;
        accountSequence.setCounter(butchSize);
        when(accountSequenseRepository.incrementCounter(anyString(), anyInt())).thenReturn(accountSequence);
        freeAccountNumbersService.generateAccountNumbers(type, butchSize);
        verify(freeAccountNumbersRepository, times(0)).saveAll(anyList());
    }

    @Test
    public void testRetrieveAccountNumber() {
        freeAccountNumbersService.retrieveAccountNumber(type, accountNumber -> {
            this.consumerIsCalled = true;
        });
        verify(freeAccountNumbersRepository, times(1)).retrieveFirst(anyString());
        assertTrue(this.consumerIsCalled);
    }

}
