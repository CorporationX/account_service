package faang.school.accountservice.controller.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.account.FreeAccountNumberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {FreeAccountNumbersController.class})
public class FreeAccountNumbersControllerTest {
    @MockBean
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generateFreeAccountNumbersWithBatchSizeTest() throws Exception {
        mockMvc.perform(post("/api/v1/free-accounts/batchSize")
                        .param("accountType", "SAVINGS")
                        .param("accountLength", "16")
                        .param("quantity", "5"))
                .andExpect(status().isOk());

        Mockito.verify(freeAccountNumberService, Mockito.times(1))
                .generateFreeAccountNumbersWithBatchSize(any(AccountType.class), anyLong(), anyLong());
    }

    @Test
    void generateFreeAccountNumbersWithLimitTest() throws Exception {
        mockMvc.perform(post("/api/v1/free-accounts/limit")
                        .param("accountType", "SAVINGS")
                        .param("accountLength", "16")
                        .param("limit", "5"))
                .andExpect(status().isOk());

        Mockito.verify(freeAccountNumberService, Mockito.times(1))
                .generateFreeAccountNumbersWithLimit(any(AccountType.class), anyLong(), anyLong());
    }

    @Test
    void processAccountNumberTest() throws Exception {
        Mockito.when(freeAccountNumberService.processAccountNumber(any(AccountType.class))).thenReturn("2200000000000001");

        mockMvc.perform(post("/api/v1/free-accounts/process/SAVINGS"))
                .andExpect(status().isOk())
                .andExpect(content().string("2200000000000001"));

        Mockito.verify(freeAccountNumberService, Mockito.times(1))
                .processAccountNumber(any(AccountType.class));
    }
}
