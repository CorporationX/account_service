package faang.school.accountservice.controller;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class NumbersAccountControllerTest {

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
    @DisplayName("Успешная генерация свободных номеров счетов")
    void generateFreeAccountNumbersTest() throws Exception {
        AccountType accountType = AccountType.PERSONAL;
        int batchSize = 5;

        doNothing().when(freeAccountNumbersService).generateFreeAccountNumbers(accountType, batchSize);

        mockMvc.perform(post("/accounts/generate/{accountType}", accountType)
                .param("batchSize", String.valueOf(batchSize)))
            .andExpect(status().isOk())
            .andExpect(content().string("Номера счетов успешно сгенерированы для типа: PERSONAL"))
            .andExpect(content().encoding("UTF-8"));

        verify(freeAccountNumbersService, times(1)).generateFreeAccountNumbers(accountType, batchSize);
    }


    @Test
    @DisplayName("Успешное получение свободного номера счета")
    void retrieveFreeAccountNumbersTest() throws Exception {
        AccountType accountType = AccountType.PERSONAL;

        doNothing().when(freeAccountNumbersService).retrieveFreeAccountNumber(eq(accountType), any());

        mockMvc.perform(get("/accounts/retrieve/{accountType}", accountType))
            .andExpect(status().isOk())
            .andExpect(content().string("Номер счета успешно получен."))
            .andExpect(content().encoding("UTF-8"));

        verify(freeAccountNumbersService, times(1)).retrieveFreeAccountNumber(eq(accountType), any());
    }


}
