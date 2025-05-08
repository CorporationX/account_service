package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.service.SavingsAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = SavingsAccountController.class)
public class SavingsAccountControllerTest {

    private final Long id = 1L;

    @MockBean
    private SavingsAccountService savingsAccountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPositiveOpenSavingsAccount() throws Exception {
        SavingsAccountResponse response = createResponse(createTariff(id));
        when(savingsAccountService.openSavingsAccount(id)).thenReturn(response);

        mockMvc.perform(post("/accounts/savings/{tariffId}", id))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testPositiveGetSavingsAccountById() throws Exception {
        SavingsAccountResponse response = createResponse(createTariff(id));
        when(savingsAccountService.getSavingsAccountById(id)).thenReturn(response);

        mockMvc.perform(get("/accounts/savings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testPositiveGetSavingsAccountByOwnerId() throws Exception {
        SavingsAccountResponse response = createResponse(createTariff(id));
        when(savingsAccountService.getSavingsAccountByOwnerId(id)).thenReturn(response);

        mockMvc.perform(get("/accounts/savings/account/{ownerId}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testPositiveUpdateTariffOnSavingsAccount() throws Exception {
        doNothing().when(savingsAccountService).updateTariffOnSavingsAccount(id, id);

        mockMvc.perform(patch("/accounts/savings/{accountId}/tariff/{tariffId}", id, id))
                .andExpect(status().isOk());
    }

    private SavingsAccountResponse createResponse(Tariff tariff) {
        return SavingsAccountResponse.builder()
                .activeTariff(tariff.getTypeName())
                .activeTariffRate(BigDecimal.valueOf(0).toString())
                .build();
    }

    private Tariff createTariff(Long tariffId) {
        return Tariff.builder()
                .id(tariffId)
                .typeName("test")
                .build();
    }
}
