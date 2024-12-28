package faang.school.accountservice.controller;

import faang.school.accountservice.dto.cashbackdto.CashbackMappingDto;
import faang.school.accountservice.dto.cashbackdto.CashbackMappingType;
import faang.school.accountservice.model.CashbackOperationMapping;
import faang.school.accountservice.model.CashbackMerchantMapping;
import faang.school.accountservice.model.CashbackTariff;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CashbackTariffControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CashbackTariffRepository tariffRepository;

    @Autowired
    private CashbackOperationMappingRepository operationMappingRepository;

    @Autowired
    private CashbackMerchantMappingRepository merchantMappingRepository;

    private Long tariffId;

    @BeforeEach
    void setUp() {
        CashbackTariff tariff = new CashbackTariff();
        tariff.setCreatedAt(LocalDateTime.now());
        tariffRepository.save(tariff);
        tariffId = tariff.getId();
    }

    @Test
    void testCreateTariff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tariffs/create")
                        .header("x-user-id", "1")) // Added header
                .andExpect(status().isCreated());
    }

    @Test
    void testGetTariffById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tariffs/{id}", tariffId)
                        .header("x-user-id", "1")) // Added header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tariffId.toString()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.operationMappings").isEmpty())
                .andExpect(jsonPath("$.merchantMappings").isEmpty());
    }

    @Test
    void testAddMappingOperation() throws Exception {
        CashbackMappingDto mappingDto = new CashbackMappingDto(
                CashbackMappingType.OPERATION,
                "purchase",
                new BigDecimal("5.00")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mappingType\": \"OPERATION\", \"key\": \"purchase\", \"percent\": 5.00}"))
                .andExpect(status().isOk());

        List<CashbackOperationMapping> operationMappings = operationMappingRepository.findByTariffId(tariffId);
        assert operationMappings.size() == 1;
        assert operationMappings.get(0).getOperationType().equals("purchase");
        assert operationMappings.get(0).getCashbackPercent().equals(new BigDecimal("5.00"));
    }

    @Test
    void testAddMappingMerchant() throws Exception {
        CashbackMappingDto mappingDto = new CashbackMappingDto(
                CashbackMappingType.MERCHANT,
                "store123",
                new BigDecimal("3.00")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mappingType\": \"MERCHANT\", \"key\": \"store123\", \"percent\": 3.00}"))
                .andExpect(status().isOk());

        List<CashbackMerchantMapping> merchantMappings = merchantMappingRepository.findByTariffId(tariffId);
        assert merchantMappings.size() == 1;
        assert merchantMappings.get(0).getMerchant().equals("store123");
        assert merchantMappings.get(0).getCashbackPercent().equals(new BigDecimal("3.00"));
    }

    @Test
    void testUpdateMappingOperation() throws Exception {
        CashbackMappingDto initialMapping = new CashbackMappingDto(
                CashbackMappingType.OPERATION,
                "purchase",
                new BigDecimal("5.00")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mappingType\": \"OPERATION\", \"key\": \"purchase\", \"percent\": 5.00}"))
                .andExpect(status().isOk());

        CashbackMappingDto updatedMapping = new CashbackMappingDto(
                CashbackMappingType.OPERATION,
                "purchase",
                new BigDecimal("10.00")
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mappingType\": \"OPERATION\", \"key\": \"purchase\", \"percent\": 10.00}"))
                .andExpect(status().isOk());

        CashbackOperationMapping operationMapping = operationMappingRepository.findByTariffIdAndOperationType(tariffId, "purchase")
                .orElse(null);
        assert operationMapping != null;
        assert operationMapping.getCashbackPercent().equals(new BigDecimal("10.00"));
    }

    @Test
    void testDeleteMapping() throws Exception {
        CashbackMappingDto mappingDto = new CashbackMappingDto(
                CashbackMappingType.MERCHANT,
                "store123",
                new BigDecimal("3.00")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mappingType\": \"MERCHANT\", \"key\": \"store123\", \"percent\": 3.00}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .param("mappingKey", "store123"))
                .andExpect(status().isOk());

        List<CashbackMerchantMapping> merchantMappings = merchantMappingRepository.findByTariffId(tariffId);
        assert merchantMappings.isEmpty();
    }

    @Test
    void testGetNonExistentTariff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tariffs/{id}", 999999999L)
                        .header("x-user-id", "1")) // Added header
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistentMapping() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tariffs/{id}/mappings", tariffId)
                        .header("x-user-id", "1") // Added header
                        .param("mappingKey", "nonExistentKey"))
                .andExpect(status().isOk());
    }
}