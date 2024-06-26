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
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${account.number.batch.size}")
    private int batchSize;

    private final long SAVINGS_PATTERN = 5236_0000_0000_0000L;
    private final long DEBIT_PATTERN = 4200_0000_0000_0000L;

    @BeforeEach
    public void setUp() {
        accountTypeDebit = AccountType.DEBIT;
        accountTypeSavings = AccountType.SAVINGS;
    }

    @Test
    public void testGenerateAccountNumbersDebit() {
        AccountNumberSequence accountNumberSequence = AccountNumberSequence
                .builder()
                .accountType(accountTypeDebit)
                .currentCounter(10L)
                .previousCounter(5L)
                .build();
        List<FreeAccountNumber> expectedAccountNumbers = new ArrayList<>();

        when(accountNumbersSequenceRepository.incrementCounter(accountTypeDebit.name(), batchSize)).thenReturn(accountNumberSequence);

        for (long i = accountNumberSequence.getPreviousCounter(); i < accountNumberSequence.getCurrentCounter(); i++) {
            expectedAccountNumbers.add(new FreeAccountNumber(accountTypeDebit, DEBIT_PATTERN + i));
        }

        freeAccountNumbersService.generateAccountNumbers(accountTypeDebit, batchSize);

        verify(accountNumbersSequenceRepository, timeout(1)).incrementCounter(accountTypeDebit.name(), batchSize);
        verify(freeAccountNumbersRepository, times(1)).saveAll(expectedAccountNumbers);
    }

    @Test
    public void testGenerateAccountNumbersSavings() {
        AccountNumberSequence accountNumberSequence = AccountNumberSequence
                .builder()
                .accountType(accountTypeSavings)
                .currentCounter(10L)
                .previousCounter(5L)
                .build();
        List<FreeAccountNumber> expectedAccountNumbers = new ArrayList<>();

        when(accountNumbersSequenceRepository.incrementCounter(accountTypeSavings.name(), batchSize)).thenReturn(accountNumberSequence);

        for (long i = accountNumberSequence.getPreviousCounter(); i < accountNumberSequence.getCurrentCounter(); i++) {
            expectedAccountNumbers.add(new FreeAccountNumber(accountTypeSavings, SAVINGS_PATTERN + i));
        }

        freeAccountNumbersService.generateAccountNumbers(accountTypeSavings, batchSize);

        verify(accountNumbersSequenceRepository, timeout(1)).incrementCounter(accountTypeSavings.name(), batchSize);
        verify(freeAccountNumbersRepository, times(1)).saveAll(expectedAccountNumbers);
    }

    @Test
    public void testGenerateAccountNumbersUpToQuantity() {
        AccountNumberSequence accountNumberSequence = AccountNumberSequence
                .builder()
                .accountType(accountTypeDebit)
                .currentCounter(5L)
                .previousCounter(0L)
                .build();

        when(freeAccountNumbersRepository.getCurrentQuantityOfNumbersByType(accountTypeDebit.name())).thenReturn(5);
        when(accountNumbersSequenceRepository.incrementCounter(accountTypeDebit.name(), 5)).thenReturn(accountNumberSequence);
        freeAccountNumbersService.generateAccountNumbersUpToQuantity(accountTypeDebit, 10);

        verify(freeAccountNumbersRepository, times(1)).saveAll(anyCollection());

    }

    @Test
    public void testGetAndHandleAccountNumber() {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber
                .builder()
                .type(AccountType.DEBIT)
                .account_number(4200_0000_0000_0000L)
                .build();

        when(freeAccountNumbersRepository.getAndDeleteAccountByType(accountTypeDebit.name())).thenReturn(freeAccountNumber);

        freeAccountNumbersService.getAndHandleAccountNumber(accountTypeDebit, numberConsumer);

        verify(freeAccountNumbersRepository, times(1)).getAndDeleteAccountByType(accountTypeDebit.name());
        verify(numberConsumer, times(1)).accept(freeAccountNumber);
    }

    @Test
    public void testGetAndHandleAccountNumberIfFanNull() {
        AccountNumberSequence accountNumberSequence = AccountNumberSequence
                .builder()
                .accountType(accountTypeDebit)
                .currentCounter(10L)
                .previousCounter(5L)
                .build();

        when(freeAccountNumbersRepository.getAndDeleteAccountByType(accountTypeDebit.name())).thenReturn(null);
        when(accountNumbersSequenceRepository.incrementCounter(accountTypeDebit.name(), 1)).thenReturn(accountNumberSequence);

        freeAccountNumbersService.getAndHandleAccountNumber(accountTypeDebit, numberConsumer);

        verify(freeAccountNumbersRepository, times(2)).getAndDeleteAccountByType(accountTypeDebit.name());
    }
}
