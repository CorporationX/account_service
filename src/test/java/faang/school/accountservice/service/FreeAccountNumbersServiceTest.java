package faang.school.accountservice.service;

import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("Позитивный тест: успешное получение и удаление свободного номера счета")
    void testGetAndRemoveFreeAccountNumber_Success() {
        AccountType accountType = AccountType.PERSONAL;
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(1L, accountType, "123456789012");
        Consumer<String> onSuccess = Mockito.mock(Consumer.class);

        when(freeAccountNumbersRepository.getAndDeleteFirstFreeAccountNumber(accountType))
            .thenReturn(Optional.of(freeAccountNumber));

        freeAccountNumbersService.getAndRemoveFreeAccountNumber(accountType, onSuccess);

        verify(freeAccountNumbersRepository).delete(freeAccountNumber);
        verify(onSuccess).accept("123456789012");
    }

    @Test
    @DisplayName("Негативный тест: ошибка при инкрементировании счетчика")
    void testGenerateNewAccountNumber_IncrementError() throws Exception {
        AccountType accountType = AccountType.PERSONAL;

        AccountNumbersSequence sequence = new AccountNumbersSequence(1L, accountType, 1L, 1L);
        when(accountNumbersSequenceRepository.findByAccountType(accountType))
            .thenReturn(Optional.of(sequence));
        when(accountNumbersSequenceRepository.incrementCounter(accountType, sequence.getVersion()))
            .thenReturn(0);

        Method method = FreeAccountNumbersService.class.getDeclaredMethod("generateNewAccountNumber", AccountType.class);
        method.setAccessible(true);

        AccountNumberGenerationException exception = null;
        try {
            method.invoke(freeAccountNumbersService, accountType);
        } catch (Exception e) {
            exception = (AccountNumberGenerationException) e.getCause();
        }

        assertNotNull(exception);
        assertEquals("Не удалось инкрементировать счетчик для типа счета: PERSONAL", exception.getMessage());
    }

    @Test
    @DisplayName("Негативный тест: ошибка при создании новой последовательности счета")
    void testGetAndRemoveFreeAccountNumber_ErrorCreatingSequence() {
        AccountType accountType = AccountType.PERSONAL;
        Consumer<String> onSuccess = Mockito.mock(Consumer.class);

        when(freeAccountNumbersRepository.getAndDeleteFirstFreeAccountNumber(accountType))
            .thenReturn(Optional.empty());

        when(accountNumbersSequenceRepository.save(Mockito.any()))
            .thenThrow(new RuntimeException("Ошибка сохранения"));

        // Выполнение метода
        AccountNumberGenerationException exception = null;
        try {
            freeAccountNumbersService.getAndRemoveFreeAccountNumber(accountType, onSuccess);
        } catch (AccountNumberGenerationException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Ошибка при создании новой последовательности счета", exception.getMessage());
    }

    @Test
    @DisplayName("Тест: создание новой последовательности счета")
    void testCreateNewAccountNumberSequence_Success() throws Exception {
        AccountType accountType = AccountType.PERSONAL;

        AccountNumbersSequence newSequence = new AccountNumbersSequence(1L, accountType, 0L, 1L);
        when(accountNumbersSequenceRepository.save(Mockito.any())).thenReturn(newSequence);

        Method method = FreeAccountNumbersService.class.getDeclaredMethod("createNewAccountNumberSequence", AccountType.class);
        method.setAccessible(true);

        AccountNumbersSequence savedSequence = (AccountNumbersSequence) method.invoke(freeAccountNumbersService, accountType);

        assertNotNull(savedSequence);
        assertEquals(accountType, savedSequence.getAccountType());
        assertEquals(0L, savedSequence.getCurrentValue());
        assertEquals(1L, savedSequence.getVersion());
    }
}
