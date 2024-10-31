package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.model.dto.TariffDto;
import faang.school.accountservice.util.ContainerCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class SavingsAccountControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = ContainerCreator.POSTGRES_CONTAINER;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Test
    void testGetSavingsAccountById() throws Exception {
        Long id = 2L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/savings-account/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.rate").value("2.4"))
                .andExpect(jsonPath("$.tariffId").value("3"))
                .andExpect(jsonPath("$.lastDatePercent").hasJsonPath())
                .andReturn();
    }

    @Test
    void testGetSavingsAccountByUserId() throws Exception {
        Long userId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/savings-account")
                        .param("userId", userId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].accountId").value(3))
                .andExpect(jsonPath("$[0].rate").value("2.4"))
                .andExpect(jsonPath("$[0].tariffId").value("3"))
                .andExpect(jsonPath("$[0].lastDatePercent").hasJsonPath())
                .andReturn();
    }

    @Test
    void testOpenSavingsAccount() throws Exception {
        // TODO asdf

        SavingsAccountDto savingsAccountDto = new SavingsAccountDto();
        savingsAccountDto.setAccountId(5L);
        savingsAccountDto.setTariffId(2L);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(savingsAccountDto);

        mockMvc.perform(post("/api/v1/savings-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].accountId").value(3))
                .andExpect(jsonPath("$[0].rate").value("2.4"))
                .andExpect(jsonPath("$[0].tariffId").value("3"))
                .andExpect(jsonPath("$[0].lastDatePercent").hasJsonPath())
                .andReturn();
    }
//    59728975298
//    @Test
//    void testCreateTariff() throws Exception {
//        String name = "NewTariff";
//        double rate = 8.1;
//        TariffDto tariffDto = new TariffDto();
//        tariffDto.setName(name);
//        tariffDto.setRate(rate);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(tariffDto);
//
//        mockMvc.perform(post("/api/v1/tariff")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.id").value(4))
//                .andExpect(jsonPath("$.name").value(name))
//                .andExpect(jsonPath("$.rate").value(rate))
//                .andReturn();
//    }
}
