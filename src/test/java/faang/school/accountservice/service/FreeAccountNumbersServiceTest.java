package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.NoFreeAccountNumbersException;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumbersServiceTest {

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;

    @Test
    @DisplayName("Позитивный тест: Генерация свободных номеров счетов для типа PERSONAL")
    void generateFreeAccountNumbersPositiveTest() {
        AccountType accountType = AccountType.PERSONAL;
        int batchSize = 5;

        doNothing().when(accountNumbersSequenceRepository).incrementCounter(anyInt(), eq(batchSize));

        freeAccountNumbersService.generateFreeAccountNumbers(accountType, batchSize);

        verify(accountNumbersSequenceRepository, times(1)).incrementCounter(accountType.ordinal(), batchSize);
    }

    @Test
    @DisplayName("Негативный тест: Нет доступных свободных номеров для типа счета")
    void retrieveFreeAccountNumbersNoAvailableTest() {
        AccountType accountType = AccountType.PERSONAL;

        when(freeAccountNumbersRepository.findFirstFreeAccountNumber(accountType.name())).thenReturn(null);

        try {
            freeAccountNumbersService.retrieveFreeAccountNumbers(accountType, freeAccountNumber -> {});
        } catch (NoFreeAccountNumbersException e) {
            assert(e.getMessage().contains("Нет доступных свободных номеров для типа счета"));
        }
    }
}
