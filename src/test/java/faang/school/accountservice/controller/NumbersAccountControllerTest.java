package faang.school.accountservice.controller;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NumbersAccountControllerTest {

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @InjectMocks
    private NumbersAccountController numbersAccountController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(numbersAccountController).build();
    }

    @Test
    @DisplayName("Позитивный тест: Генерация свободных номеров счетов для типа PERSONAL")
    void generateFreeAccountNumbersPositiveTest() throws Exception {
        // Параметры для теста
        AccountType accountType = AccountType.PERSONAL;
        int batchSize = 5;

        // Выполнение запроса и проверка ответа
        mockMvc.perform(post("/accounts/generate/{accountType}", accountType)
                .param("batchSize", String.valueOf(batchSize))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Номера счетов успешно сгенерированы."));

        // Проверка, что сервис был вызван
        verify(freeAccountNumbersService, times(1)).generateFreeAccountNumbers(accountType, batchSize);
    }

    @Test
    @DisplayName("Позитивный тест: Получение свободного номера счета для типа PERSONAL")
    void retrieveFreeAccountNumbersPositiveTest() throws Exception {
        // Параметры для теста
        AccountType accountType = AccountType.PERSONAL;

        // Выполнение запроса и проверка ответа
        mockMvc.perform(get("/accounts/retrieve/{accountType}", accountType)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Номер счета успешно получен."));

        // Проверка, что сервис был вызван
        verify(freeAccountNumbersService, times(1)).retrieveFreeAccountNumbers(eq(accountType), any());
    }

    @Test
    @DisplayName("Негативный тест: Генерация свободных номеров счетов с неверным типом счета")
    void generateFreeAccountNumbersInvalidTypeTest() throws Exception {
        // Параметры для теста
        AccountType accountType = AccountType.valueOf("INVALID_TYPE"); // Некорректный тип счета
        int batchSize = 5;

        // Выполнение запроса и проверка ответа на ошибку
        mockMvc.perform(post("/accounts/generate/{accountType}", accountType)
                .param("batchSize", String.valueOf(batchSize))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())  // Здесь можно ожидать 400 Bad Request
            .andExpect(content().string("Ошибка при генерации свободных номеров счетов"));

        // Проверка, что сервис не был вызван
        verify(freeAccountNumbersService, times(0)).generateFreeAccountNumbers(accountType, batchSize);
    }

    @Test
    @DisplayName("Негативный тест: Получение свободного номера счета для несуществующего типа счета")
    void retrieveFreeAccountNumbersNotFoundTest() throws Exception {
        // Параметры для теста
        AccountType accountType = AccountType.PERSONAL;

        // Мокаем поведение сервиса для ошибки
        doThrow(new RuntimeException("Ошибка при получении свободного номера счета"))
            .when(freeAccountNumbersService).retrieveFreeAccountNumbers(eq(accountType), any());

        // Выполнение запроса и проверка ошибки
        mockMvc.perform(get("/accounts/retrieve/{accountType}", accountType)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())  // Ошибка 500 при проблемах в сервисе
            .andExpect(content().string("Ошибка при получении свободного номера счета"));

        // Проверка, что сервис был вызван
        verify(freeAccountNumbersService, times(1)).retrieveFreeAccountNumbers(eq(accountType), any());
    }
}
