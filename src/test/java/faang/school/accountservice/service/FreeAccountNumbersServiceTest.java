package faang.school.accountservice.service;

import faang.school.accountservice.model.AccountNumber;
import faang.school.accountservice.model.AccountNumberId;
import faang.school.accountservice.model.AccountSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumbersServiceTest {
    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;
    @Value("${account_numbers.bathSize}")
    private int batchSize;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(freeAccountNumbersService, "batchSize", 1);
    }

    @Test
    public void generatedNumberTest() {
        AccountSequence accountSequence = new AccountSequence();
        accountSequence.setType("debit");
        accountSequence.setCounter(2);
        accountSequence.setInitialValue(1);
        List<AccountNumber> accountNumbers = new ArrayList<>();
        AccountNumberId debit = new AccountNumberId(4200_0000_0000_0000L, "debit");
        AccountNumber accountNumber = new AccountNumber(debit);
        accountNumbers.add(accountNumber);
        Mockito.when(accountNumbersSequenceRepository.incrementCounter("debit", 1)).thenReturn(accountSequence);
        Mockito.when(freeAccountNumbersRepository.saveAll(Mockito.anyList())).thenReturn(new ArrayList<>());

        freeAccountNumbersService.generatedNumbers("debit");

        Mockito.verify(accountNumbersSequenceRepository, Mockito.times(1)).incrementCounter("debit", 1);
        Mockito.verify(freeAccountNumbersRepository, Mockito.times(1)).saveAll(Mockito.anyList());

    }

}
