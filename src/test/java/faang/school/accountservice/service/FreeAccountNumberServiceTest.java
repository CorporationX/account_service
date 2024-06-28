package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumberServiceTest {

    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Mock
    private Consumer<FreeAccountNumber> numberConsumer;

    private AccountType accountTypeDebit;
    private AccountType accountTypeSavings;
    private int batchSize;
    private AccountNumberSequence accountNumberSequence;
    private List<FreeAccountNumber> expectedAccountNumbers;
    private FreeAccountNumber freeAccountNumber;


    @BeforeEach
    public void setUp() {
        batchSize = 5;
        accountTypeDebit = AccountType.DEBIT;
        accountTypeSavings = AccountType.SAVINGS;
        expectedAccountNumbers = new ArrayList<>();

        freeAccountNumber = FreeAccountNumber
                .builder()
                .type(AccountType.DEBIT)
                .accountNumber(accountTypeDebit.getPattern())
                .build();

        accountNumberSequence = AccountNumberSequence
                .builder()
                .accountType(accountTypeDebit)
                .currentCounter(10L)
                .previousCounter(5L)
                .build();
    }

    @Test
    public void testGenerateAccountNumbersDebit() {
        when(accountNumbersSequenceRepository.incrementCounter(accountTypeDebit.name(), batchSize)).thenReturn(accountNumberSequence);

        for (long i = accountNumberSequence.getPreviousCounter(); i < accountNumberSequence.getCurrentCounter(); i++) {
            expectedAccountNumbers.add(new FreeAccountNumber(accountTypeDebit, accountTypeDebit.getPattern() + i));
        }

        freeAccountNumbersService.generateAccountNumbers(accountTypeDebit, batchSize);

        verify(accountNumbersSequenceRepository, timeout(1)).incrementCounter(accountTypeDebit.name(), batchSize);
        verify(freeAccountNumbersRepository, times(1)).saveAll(expectedAccountNumbers);
    }

    @Test
    public void testGenerateAccountNumbersSavings() {
        when(accountNumbersSequenceRepository.incrementCounter(accountTypeSavings.name(), batchSize)).thenReturn(accountNumberSequence);

        for (long i = accountNumberSequence.getPreviousCounter(); i < accountNumberSequence.getCurrentCounter(); i++) {
            expectedAccountNumbers.add(new FreeAccountNumber(accountTypeSavings, accountTypeSavings.getPattern() + i));
        }

        freeAccountNumbersService.generateAccountNumbers(accountTypeSavings, batchSize);

        verify(accountNumbersSequenceRepository, timeout(1)).incrementCounter(accountTypeSavings.name(), batchSize);
        verify(freeAccountNumbersRepository, times(1)).saveAll(expectedAccountNumbers);
    }

    @Test
    public void testGenerateAccountNumbersUpToQuantity() {
        when(freeAccountNumbersRepository.getCurrentQuantityOfNumbersByType(accountTypeDebit.name())).thenReturn(5);
        when(accountNumbersSequenceRepository.incrementCounter(accountTypeDebit.name(), 5)).thenReturn(accountNumberSequence);
        freeAccountNumbersService.generateAccountNumbersUpToQuantity(accountTypeDebit, 10);

        verify(freeAccountNumbersRepository, times(1)).saveAll(anyCollection());
    }

    @Test
    public void testGetAndHandleAccountNumber() {
        when(freeAccountNumbersRepository.getAndDeleteAccountByType(accountTypeDebit.name())).thenReturn(freeAccountNumber);

        freeAccountNumbersService.retrieveAndHandleAccountNumber(accountTypeDebit, numberConsumer);

        verify(freeAccountNumbersRepository, times(1)).getAndDeleteAccountByType(accountTypeDebit.name());
        verify(numberConsumer, times(1)).accept(freeAccountNumber);
    }

    @Test
    public void testRetrieveAndHandleAccountNumberIfFanNull() {
        when(freeAccountNumbersRepository.getAndDeleteAccountByType(accountTypeDebit.name())).thenReturn(null);
        when(accountNumbersSequenceRepository.incrementCounter(accountTypeDebit.name(), 1)).thenReturn(accountNumberSequence);

        freeAccountNumbersService.retrieveAndHandleAccountNumber(accountTypeDebit, numberConsumer);

        verify(freeAccountNumbersRepository, times(2)).getAndDeleteAccountByType(accountTypeDebit.name());
    }
}
