package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class SavingsAccountControllerTest {

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/savings-account/{id}", 2L)
                        .header("x-user-id", 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.rate").value("2.4"))
                .andExpect(jsonPath("$.tariffId").value("3"))
                .andExpect(jsonPath("$.lastDatePercent").hasJsonPath())
                .andReturn();
    }

    @Test
    void testGetSavingsAccountByUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/savings-account")
                        .header("x-user-id", 2L)
                        .param("userId", Long.toString(2L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].rate").value("2.4"))
                .andExpect(jsonPath("$[0].tariffId").value("3"))
                .andExpect(jsonPath("$[0].lastDatePercent").hasJsonPath())
                .andReturn();
    }
}