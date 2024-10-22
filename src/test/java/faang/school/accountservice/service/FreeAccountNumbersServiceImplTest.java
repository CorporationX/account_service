package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumbersServiceImplTest {

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @Test
    public void testGenerateNumberByType_FoundNumber() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;
        String expectedAccountNumber = "4200000000000001";

        when(freeAccountNumbersRepository.getFreeAccountNumberByType(type.name()))
                .thenReturn(Optional.of(expectedAccountNumber));

        String result = freeAccountNumbersService.generateNumberByType(type);

        assertNotNull(result);
        assertEquals(expectedAccountNumber, result);
        verify(accountNumbersSequenceRepository, never()).incrementAndGet(anyString());
        verify(accountNumberGenerator, never()).generateAccountNumber(any(), anyLong());
    }

    @Test
    public void testGenerateNumberByType_NotFoundNumber() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;
        Long sequenceNumber = 420L;
        String generatedAccountNumber = "4200000000000420";

        when(freeAccountNumbersRepository.getFreeAccountNumberByType(type.name()))
                .thenReturn(Optional.empty());
        when(accountNumbersSequenceRepository.incrementAndGet(type.name()))
                .thenReturn(sequenceNumber);
        when(accountNumberGenerator.generateAccountNumber(type, sequenceNumber))
                .thenReturn(generatedAccountNumber);

        String result = freeAccountNumbersService.generateNumberByType(type);

        assertNotNull(result);
        assertEquals(generatedAccountNumber, result);
        verify(accountNumbersSequenceRepository).incrementAndGet(type.name());
        verify(accountNumberGenerator).generateAccountNumber(type, sequenceNumber);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateNumberAndExecute() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;
        String accountNumber = "4200000000000001";
        Consumer<FreeAccountNumber> mockConsumer = mock(Consumer.class);
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(FreeAccountNumberId.builder().type(type).accountNumber(accountNumber).build())
                .build();
        when(freeAccountNumbersService.generateNumberByType(type)).thenReturn(accountNumber);

        freeAccountNumbersService.generateNumberAndExecute(type, mockConsumer);

        verify(mockConsumer).accept(freeAccountNumber);
    }

    @Test
    public void testSaveNumber() {
        AccountType type = AccountType.CHECKING_INDIVIDUAL;
        String accountNumber = "4200000000000001";
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(type, accountNumber))
                .build();

        freeAccountNumbersService.saveNumber(type, accountNumber);

        verify(freeAccountNumbersRepository).save(freeAccountNumber);
    }
}
