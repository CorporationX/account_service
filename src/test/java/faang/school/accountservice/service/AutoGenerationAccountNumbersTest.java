package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AutoGenerationAccountNumbersTest {

    @InjectMocks
    private AutoGenerationAccountNumbers autoGenerationAccountNumbers;

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    private int batchSize;

    public void setUp() {
        batchSize = 5;
    }

    @Test
    public void testGenerationDebitAccountNumbers() {
        autoGenerationAccountNumbers.generationDebitAccountNumbers();
        verify(freeAccountNumbersService, times(1)).generateAccountNumbersUpToQuantity(AccountType.DEBIT, batchSize);
    }

    @Test
    public void testGenerationSavingsAccountNumbers() {
        autoGenerationAccountNumbers.generationSavingsAccountNumbers();
        verify(freeAccountNumbersService, times(1)).generateAccountNumbersUpToQuantity(AccountType.SAVINGS, batchSize);
    }
}
