package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountGenerationException;
import faang.school.accountservice.exception.NoFreeAccountNumbersException;
import faang.school.accountservice.model.FreeAccountId;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FreeAccountNumbersServiceTest {

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешная генерация свободных номеров счетов для типа счета PERSONAL")
    void generateFreeAccountNumbersSuccess() {
        AccountType accountType = AccountType.PERSONAL;
        int batchSize = 5;

        Mockito.doNothing().when(accountNumbersSequenceRepository).incrementCounter(accountType, batchSize);

        freeAccountNumbersService.generateFreeAccountNumbers(accountType, batchSize);

        verify(freeAccountNumbersRepository, times(1)).saveAll(any());  // Проверка вызова saveAll
    }


    @Test
    @DisplayName("Ошибка при генерации свободных номеров счетов с неизвестным типом счета")
    void generateFreeAccountNumbersInvalidAccountType() {
        AccountType accountType = null;  // Параметр типа счета null, чтобы смоделировать ошибку
        int batchSize = 5;

        AccountGenerationException exception = assertThrows(AccountGenerationException.class, () -> {
            freeAccountNumbersService.generateFreeAccountNumbers(accountType, batchSize);
        });

        assertEquals("Ошибка при генерации свободных номеров счетов", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное получение свободного номера счета для типа счета PERSONAL")
    void retrieveFreeAccountNumberSuccess() {
        AccountType accountType = AccountType.PERSONAL;
        long accountNumber = 4200000000000000L; // Пример номера счета
        FreeAccountId freeAccountId = new FreeAccountId(accountType, accountNumber);
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);

        Mockito.when(freeAccountNumbersRepository.findFirstFreeAccountNumber(accountType.name())).thenReturn(freeAccountNumber);
        Consumer<FreeAccountNumber> numberConsumer = number -> {}; // Пустой consumer

        freeAccountNumbersService.retrieveFreeAccountNumber(accountType, numberConsumer);

        verify(freeAccountNumbersRepository, times(1)).deleteByAccountTypeAndAccountNumber(eq(accountType.name()), eq(accountNumber));
    }

    @Test
    @DisplayName("Ошибка при попытке получить свободный номер счета, когда их нет")
    void retrieveFreeAccountNumberNoFreeNumbers() {
        AccountType accountType = AccountType.PERSONAL;

        Mockito.when(freeAccountNumbersRepository.findFirstFreeAccountNumber(accountType.name())).thenReturn(null);

        NoFreeAccountNumbersException exception = assertThrows(NoFreeAccountNumbersException.class, () -> {
            freeAccountNumbersService.retrieveFreeAccountNumber(accountType, number -> {});
        });

        assertEquals("Нет доступных свободных номеров для типа счета: PERSONAL", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка при генерации свободных номеров счетов - неожиданная ошибка")
    void generateFreeAccountNumbersUnexpectedError() {
        // Параметры для теста
        AccountType accountType = AccountType.PERSONAL;
        int batchSize = 5;

        Mockito.doThrow(new RuntimeException("Unexpected error")).when(accountNumbersSequenceRepository).incrementCounter(accountType, batchSize);

        AccountGenerationException exception = assertThrows(AccountGenerationException.class, () -> {
            freeAccountNumbersService.generateFreeAccountNumbers(accountType, batchSize);
        });

        assertTrue(exception.getMessage().contains("Ошибка при генерации свободных номеров счетов"));
    }
}

